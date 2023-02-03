package com.lucidworks.fusion.semanticsearch.query;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.client.impl.InternalSolrJClient;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import com.lucidworks.fusion.semanticsearch.query.spellcheck.SpellcheckConfig;
import com.lucidworks.fusion.semanticsearch.query.spellcheck.SpellcheckProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

@Slf4j
public class SpellcheckStageRequestHandler extends RequestHandlerBase {

    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) {
        SolrParams params = req.getParams();

        try {
            SpellcheckConfig spellcheckConfig = new SpellcheckConfig(params.get("spellCheckerCollection"),
                                                                     params.get("accuracy"));

            String dot = params.get("graph");
            SemanticGraph graph = SemanticGraph.fromDot(dot);

            RestClient client = new InternalSolrJClient(req.getCore());

            SpellcheckProcessor spellcheck = new SpellcheckProcessor();
            graph = spellcheck.process(client, spellcheckConfig, graph);
            rsp.add("graph", graph.toDot());
        } catch (Exception e) {
            rsp.add("graph", StackTraceExtractor.extract(e));
        }
    }

    public String getDescription() {
        return "This is a request handler that temporary mimics " +
                "the Semantic Graph Spellcheck Stage form Fusion.";
    }

}