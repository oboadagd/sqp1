package com.lucidworks.fusion.semanticsearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.client.impl.InternalSolrJClient;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import com.lucidworks.fusion.semanticsearch.query.initialization.BasicInitializer;
import com.lucidworks.fusion.semanticsearch.query.initialization.BasicInitializerConfig;
import com.lucidworks.fusion.semanticsearch.query.initialization.GraphBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import java.util.HashMap;

@Slf4j
public class InitializationStageRequestHandler extends RequestHandlerBase {

    private static final String QUERY = "query";
    private static final String GRAPH = "graph";

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) {
        HashMap<String, Object> params = new HashMap<>();
        req.getParams().toMap(params);

        try {
            BasicInitializerConfig config = mapper.convertValue(params, BasicInitializerConfig.class);

            RestClient client = new InternalSolrJClient(req.getCore());
            GraphBuilder builder = new GraphBuilder();
            BasicInitializer initializer = new BasicInitializer(client, builder);

            String query = (String) params.get(QUERY);
            SemanticGraph graph = initializer.initialize(config, query);
            rsp.add(GRAPH, graph.toDot());
        } catch (Exception e) {
            rsp.add(GRAPH, StackTraceExtractor.extract(e));
        }
    }

    @Override
    public String getDescription() {
        return "This is a custom request handler that make token collection from request text";
    }
}