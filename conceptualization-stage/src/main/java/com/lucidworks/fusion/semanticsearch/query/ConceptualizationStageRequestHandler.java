package com.lucidworks.fusion.semanticsearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.client.impl.InternalSolrJClient;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import com.lucidworks.fusion.semanticsearch.query.conceptualization.ConceptualizationConfig;
import com.lucidworks.fusion.semanticsearch.query.conceptualization.ConceptualizationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import java.util.HashMap;


@SuppressWarnings("unused")
@Slf4j
public class ConceptualizationStageRequestHandler extends RequestHandlerBase {

    private final ObjectMapper mapper = new ObjectMapper();
    private final SemanticProcessor<ConceptualizationConfig> processor = new ConceptualizationProcessor();

    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) {
        HashMap<String, Object> params = new HashMap<>();
        req.getParams().toMap(params);

        try {
            ConceptualizationConfig config = mapper.convertValue(params, ConceptualizationConfig.class);
            log.info("====> got config: " + config.toString());

            String dot = (String) params.get("graph");
            SemanticGraph graph = SemanticGraph.fromDot(dot);
            log.info("====> got graph: " + graph.toString());

            RestClient client = new InternalSolrJClient(req.getCore());
            SemanticGraph calculatedGraph = processor.process(client, config, graph);
            rsp.add("graph", calculatedGraph.toDot());
        } catch (Exception e) {
            rsp.add("graph", StackTraceExtractor.extract(e));
        }
    }

    public String getDescription() {
        return "This is a request handler that temporary mimics " +
                "the Semantic Graph Conceptualization Stage form Fusion.";
    }
}