package com.lucidworks.fusion.semanticsearch.query.weighting;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.model.Field;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.weighting.model.FieldWeight;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class WeightingProcessor implements SemanticProcessor<WeightingConfig> {

    private static final String ROWS_PARAM_NAME = "rows";
    private static final String ROWS_QUANTITY = "1000";
    private static final String QUERY_PARAM_NAME = "q";
    private static final String QUERY_TEMPLATE = "name:\"%s\"";
    private static final String OR_DELIMITER = " OR ";

    private final WeightingMapper mapper = new WeightingMapper();
    private final WeightingCalculator calculator = new WeightingCalculator();

    @Override
    public SemanticGraph process(RestClient client ,
                                 WeightingConfig config,
                                 SemanticGraph graph) throws Exception {
        Map<String, Object> response = getWeightingResponse(graph, client, config);
        List<FieldWeight> weights = mapper.mapResponse(response);
        return calculator.calculate(graph, weights, config);
    }

    private Map<String, Object> getWeightingResponse(SemanticGraph graph,
                                                     RestClient client,
                                                     WeightingConfig config) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ROWS_PARAM_NAME, ROWS_QUANTITY);
        params.put(QUERY_PARAM_NAME, getQuery(graph));
        return client.get(config.getWeightingCollection() + config.getEndpoint(), params);
    }

    private String getQuery(SemanticGraph graph) {
        return graph.edgeSet().stream()
                .map(SemanticEdge::getFields)
                .flatMap(Collection::stream)
                .map(Field::getName)
                .distinct()
                .map(name -> String.format(QUERY_TEMPLATE, name))
                .collect(joining(OR_DELIMITER));
    }
}
