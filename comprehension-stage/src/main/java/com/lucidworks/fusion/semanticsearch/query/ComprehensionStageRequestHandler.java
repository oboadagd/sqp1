package com.lucidworks.fusion.semanticsearch.query;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.client.impl.InternalSolrJClient;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import com.lucidworks.fusion.semanticsearch.query.comprehension.ComprehensionConfig;
import com.lucidworks.fusion.semanticsearch.query.comprehension.ComprehensionProcessor;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

public class ComprehensionStageRequestHandler extends RequestHandlerBase {

    private static final String GRAPH_PARAM = "graph";
    private static final String DICTIONARY = "dictionary";
    private static final String ENDPOINT = "endpoint";

    private final SemanticProcessor<ComprehensionConfig> processor = new ComprehensionProcessor();

    @Override
    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) {
        try {
            SolrParams solrParams = req.getParams();
            String dot = solrParams.get(GRAPH_PARAM);
            SemanticGraph graph = SemanticGraph.fromDot(dot);

            String dictionary = solrParams.get(DICTIONARY);
            String endpoint = solrParams.get(ENDPOINT);
            ComprehensionConfig config = new ComprehensionConfig(dictionary, endpoint);

            RestClient client = new InternalSolrJClient(req.getCore());
            SemanticGraph calculatedGraph = processor.process(client, config, graph);
            rsp.add(GRAPH_PARAM, calculatedGraph.toDot());
        } catch (Exception e) {
            rsp.add(GRAPH_PARAM, StackTraceExtractor.extract(e));
        }
    }

    @Override
    public String getDescription() {
        return "This is a request handler that temporary mimics " +
                "the Semantic Graph Comprehension Stage form Fusion.";
    }
}
