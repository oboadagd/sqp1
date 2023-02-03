package com.lucidworks.fusion.boosting.sqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticTransformer;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SQPBoostingStrategyStageRequestHandler extends RequestHandlerBase {

    private final ObjectMapper mapper = new ObjectMapper();
    private final SemanticTransformer<SQPBoostingStrategyConfig> transformer = new SQPBoostingStrategyTransformer();

    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) {
        Map<String, Object> params = new HashMap<>();
        req.getParams().toMap(params);

        try {
            SQPBoostingStrategyConfig config = mapper.convertValue(params, SQPBoostingStrategyConfig.class);

            String dot = config.getGraph();
            SemanticGraph graph = SemanticGraph.fromDot(dot);

            Map<String, String> queryParams = transformer.transform(config, graph);

            queryParams.forEach(rsp::add);
        } catch (Exception e) {
            rsp.add("graph", StackTraceExtractor.extract(e));
        }
    }

    public String getDescription() {
        return "This is a request handler that temporary mimics " +
               "the SQP Boosting Strategy Stage from Fusion.";
    }

}
