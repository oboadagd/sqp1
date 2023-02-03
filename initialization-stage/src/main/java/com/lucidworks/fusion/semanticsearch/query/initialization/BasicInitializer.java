package com.lucidworks.fusion.semanticsearch.query.initialization;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticInitializer;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.query.initialization.model.AnalysisResponse;
import com.lucidworks.fusion.semanticsearch.query.initialization.model.AnalysisResponse.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class BasicInitializer implements SemanticInitializer<BasicInitializerConfig> {
    private static final String ENDPOINT = "/analysis/field";
    private static final String QUERY_PARAM_NAME = "q";
    private static final String ANALYSIS_PARAM_NAME = "analysis.fieldtype";
    private static final String WT_PARAM_NAME = "wt";
    private static final String WT_PARAM_VALUE = "json";

    private final RestClient client;
    private final GraphBuilder graphBuilder;

    @Override
    public SemanticGraph initialize(BasicInitializerConfig config, String query) throws Exception {
        String url = config.getProductCollection() + ENDPOINT;
        log.info("Analysis request: {} ,\n"
                        + "for the phrase: {} .",
                query, url);
        Map<String, String> params = new HashMap<>();
        params.put(QUERY_PARAM_NAME, query);
        params.put(ANALYSIS_PARAM_NAME, config.getFieldType());
        params.put(WT_PARAM_NAME, WT_PARAM_VALUE);

        AnalysisResponse response = client.get(url, params, AnalysisResponse.class);
        log.info("Analysis response: {} ,\n"
                + "for the value: {} .", response, query);

        List<Token> tokens = response.getFinalTokens().get(config.getFieldType());
        return graphBuilder.build(tokens);
    }
}
