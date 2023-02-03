package com.lucidworks.fusion.semanticsearch.query.initialization;


import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.query.initialization.model.AnalysisResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class BasicInitializerTest {
    private static final String ENDPOINT = "/analysis/field";
    private static final String PRODUCT_COLLECTION = "demo";
    private static final String FIELD_TYPE = "joined_text";
    private static final String QUERY_PARAM_NAME = "q";
    private static final String ANALYSIS_PARAM_NAME = "analysis.fieldtype";
    private static final String WT_PARAM_NAME = "wt";
    private static final String WT_PARAM_VALUE = "json";
    private static final String QUERY = "cheap cheap trick music";
    private static final List<String> WORDS = Arrays.asList("cheap", "cheap", "trick", "music");

    private BasicInitializer tested;
    private RestClient client;
    private GraphBuilder builder;

    @Before
    public void setUp() {
        client = mock(RestClient.class);
        builder = mock(GraphBuilder.class);
        tested = new BasicInitializer(client, builder);
    }

    @Test
    public void shouldReturnBuiltGraph() throws Exception {
        BasicInitializerConfig config = createConfig();
        Map<String, String> params = createParams();
        AnalysisResponse response = createResponse();
        SemanticGraph graph = mock(SemanticGraph.class);

        when(client.get(PRODUCT_COLLECTION + ENDPOINT, params, AnalysisResponse.class)).thenReturn(response);
        when(builder.build(response.getFinalTokens().get(FIELD_TYPE))).thenReturn(graph);

        SemanticGraph actual = tested.initialize(config, QUERY);

        verifyNoInteractions(graph);
        assertSame(graph, actual);
    }

    private BasicInitializerConfig createConfig() {
        BasicInitializerConfig config = new BasicInitializerConfig();
        config.setProductCollection(PRODUCT_COLLECTION);
        config.setFieldType(FIELD_TYPE);
        return config;
    }

    private Map<String, String> createParams() {
        Map<String, String> params = new HashMap<>();
        params.put(QUERY_PARAM_NAME, QUERY);
        params.put(ANALYSIS_PARAM_NAME, FIELD_TYPE);
        params.put(WT_PARAM_NAME, WT_PARAM_VALUE);
        return params;
    }

    private AnalysisResponse createResponse() {
        AnalysisResponse response = new AnalysisResponse();
        response.setFinalTokens(new HashMap<>());
        response.getFinalTokens().put(FIELD_TYPE, createTokenList());
        return response;
    }

    private List<AnalysisResponse.Token> createTokenList() {
        List<AnalysisResponse.Token> tokens = new ArrayList<>();

        for (int i = 0; i < WORDS.size(); i++) {
            AnalysisResponse.Token token = createToken(WORDS.get(i), i+1);
            tokens.add(token);
        }

        return tokens;
    }

    private AnalysisResponse.Token createToken(String text, int position) {
        AnalysisResponse.Token token = new AnalysisResponse.Token();
        token.setText(text);
        token.setPosition(position);
        return token;
    }
}