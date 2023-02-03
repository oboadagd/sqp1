package com.lucidworks.fusion.semanticsearch.query.conceptualization;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils;
import com.lucidworks.fusion.semanticsearch.query.conceptualization.model.Concept;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;


@Slf4j
public class ConceptualizationProcessor implements SemanticProcessor<ConceptualizationConfig> {

    private static final String ROWS_PARAM_NAME = "rows";
    private static final String ROWS_QUANTITY = "1000";
    private static final String QUERY_PARAM_NAME = "q";
    private static final String QUERY_TEMPLATE = "value:\"%s\"";
    private static final String OR_DELIMITER = " OR ";

    private final ConceptualizationMapper mapper = new ConceptualizationMapper();
    private final ConceptualizationCalculator calculator = new ConceptualizationCalculator();

    @Override
    public SemanticGraph process(RestClient client,
                                 ConceptualizationConfig config,
                                 SemanticGraph graph) throws Exception {

        List<GraphPath<String, SemanticEdge>> subPaths = graph.getAllSubPaths();
        Map<String, Object> response = getConceptualizationResponse(subPaths, client, config);
        List<Concept> concepts = mapper.mapResponse(response);
        return calculator.calculate(graph, subPaths, concepts);
    }

    private Map<String, Object> getConceptualizationResponse(List<GraphPath<String, SemanticEdge>> paths,
                                                     RestClient client,
                                                     ConceptualizationConfig config) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(ROWS_PARAM_NAME, ROWS_QUANTITY);
        params.put(QUERY_PARAM_NAME, getQuery(paths));
        return client.get(config.getConcepts() + config.getEndpoint(), params);
    }

    private String getQuery(List<GraphPath<String, SemanticEdge>> paths) {
        return paths.stream()
                .map(GraphPathUtils::createPhrase)
                .map(token -> String.format(QUERY_TEMPLATE, token))
                .collect(joining(OR_DELIMITER));
    }
}
