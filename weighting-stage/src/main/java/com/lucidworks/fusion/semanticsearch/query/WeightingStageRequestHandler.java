package com.lucidworks.fusion.semanticsearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.client.impl.InternalSolrJClient;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import com.lucidworks.fusion.semanticsearch.query.weighting.WeightingConfig;
import com.lucidworks.fusion.semanticsearch.query.weighting.WeightingProcessor;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import java.util.HashMap;

public class WeightingStageRequestHandler extends RequestHandlerBase {

    private final ObjectMapper mapper = new ObjectMapper();
    private final SemanticProcessor<WeightingConfig> processor = new WeightingProcessor();

    @Override
    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) {
        HashMap<String, Object> params = new HashMap<>();
        req.getParams().toMap(params);

        try {
            WeightingConfig config = mapper.convertValue(params, WeightingConfig.class);

            String dot = (String) params.get("graph");
            SemanticGraph graph = SemanticGraph.fromDot(dot);

            RestClient client = new InternalSolrJClient(req.getCore());
            SemanticGraph calculatedGraph = processor.process(client, config, graph);
            rsp.add("graph", calculatedGraph.toDot());
        } catch (Exception e) {
            rsp.add("graph", StackTraceExtractor.extract(e));
        }
    }

    @Override
    public String getDescription() {
        return "This is a request handler that temporary mimics " +
                "the Semantic Weighting Stage from Fusion.";
    }
}
