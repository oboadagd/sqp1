package com.lucidworks.fusion.semanticsearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticTransformer;
import com.lucidworks.fusion.semanticsearch.common.utilities.StackTraceExtractor;
import com.lucidworks.fusion.semanticsearch.query.preparation.SolrQueryPreparationConfig;
import com.lucidworks.fusion.semanticsearch.query.preparation.SolrQueryPreparationTransformer;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.SearchStage;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.SearchStageFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SolrQueryPreparationStageRequestHandler extends RequestHandlerBase {

    private final ObjectMapper mapper = new ObjectMapper();
    private final SemanticTransformer<SolrQueryPreparationConfig> transformer = new SolrQueryPreparationTransformer();
    private final SearchStageFactory stageFactory = new SearchStageFactory();

    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) {
        Map<String, Object> params = new HashMap<>();
        req.getParams().toMap(params);

        try {
            SolrQueryPreparationConfig config = mapper.convertValue(params, SolrQueryPreparationConfig.class);

            String dot = config.getGraph();
            SemanticGraph graph = SemanticGraph.fromDot(dot);

            SearchStage searchStage = stageFactory.createSearchStage(params);
            config.setSearchStage(searchStage);

            Map<String, String> queryParams = transformer.transform(config, graph);

            queryParams.forEach(rsp::add);
        } catch (Exception e) {
            rsp.add("graph", StackTraceExtractor.extract(e));
        }
    }

    public String getDescription() {
        return "This is a request handler that temporary mimics " +
                "the Semantic Query Preparation Stage from Fusion.";
    }
}
