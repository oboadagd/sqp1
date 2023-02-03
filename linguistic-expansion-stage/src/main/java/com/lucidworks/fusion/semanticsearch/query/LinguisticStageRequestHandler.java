package com.lucidworks.fusion.semanticsearch.query;


import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.client.impl.InternalSolrJClient;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import com.lucidworks.fusion.semanticsearch.query.process.LinguisticStageConfig;
import com.lucidworks.fusion.semanticsearch.query.process.LinguisticStageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

@Slf4j
public class LinguisticStageRequestHandler extends RequestHandlerBase {
    private static final String selectEndpoint = "/select";

    public void handleRequestBody(SolrQueryRequest request, SolrQueryResponse response) {
        log.info("Called request handler with SolrQueryRequest: " + request.toString());
        SolrParams params = request.getParams();
        try {
            var linguisticStageConfig = new LinguisticStageConfig(params.get("lingcoll"), selectEndpoint, Integer.valueOf(params.get("windowSize")));

            var graph = SemanticGraph.fromDot(params.get("graph"));
            var client = new InternalSolrJClient(request.getCore());

            var linguisticStageProcessor = new LinguisticStageProcessor();

            graph = linguisticStageProcessor.process(client, linguisticStageConfig, graph);
            log.info("graph after linguisticStageProcessor: " + graph.toDot());

            response.add("graph", graph.toDot());
            log.info("graph after multiSynonymProcessor: " + graph.toDot());
        } catch (Exception e) {
            response.add("graph", StackTraceExtractor.extract(e));
        }
    }

    public String getDescription() {
        return "This is a custom request handler that make linguistic analysis and expand graph with synonyms";
    }
}

