package com.lucidworks.fusion.semanticsearch.query.comprehension;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.model.Comprehension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class ComprehensionProcessor implements SemanticProcessor<ComprehensionConfig> {

    private static final String ROWS_PARAM_NAME = "rows";
    private static final String ROWS_QUANTITY = "1000";
    private static final String QUERY_PARAM_NAME = "q";
    private static final String QUERY_TEMPLATE = "key:\"%s\"";
    private static final String OR_DELIMITER = " OR ";

    private final ComprehensionMapper mapper = new ComprehensionMapper();
    private final ComprehensionCalculator calculator = new ComprehensionCalculator();

    @Override
    public SemanticGraph process(RestClient client,
                                 ComprehensionConfig config,
                                 SemanticGraph graph) throws Exception {

        Map<String, Object> response = getComprehensionResponse(graph, client, config);
        List<Comprehension> comprehensions = mapper.mapResponse(response);
        return calculator.calculateComprehension(graph, comprehensions);
    }

    private Map<String, Object> getComprehensionResponse(SemanticGraph graph,
                                                         RestClient client,
                                                         ComprehensionConfig config) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ROWS_PARAM_NAME, ROWS_QUANTITY);
        params.put(QUERY_PARAM_NAME, getQuery(graph));
        return client.get(config.getDictionary() + config.getEndpoint(), params);
    }

    private String getQuery(SemanticGraph graph) {
        return graph.edgeSet().stream()
                .map(SemanticEdge::getToken)
                .map(token -> String.format(QUERY_TEMPLATE, token))
                .collect(joining(OR_DELIMITER));
    }
}
