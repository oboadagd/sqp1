package com.lucidworks.fusion.semanticsearch.query.weighting;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier;
import com.lucidworks.fusion.semanticsearch.query.weighting.model.FieldWeight;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class WeightingCalculator {

    public SemanticGraph calculate(SemanticGraph graph,
                                   List<FieldWeight> weights,
                                   WeightingConfig config) {

        Map<String, FieldWeight> weightsMap = weights.stream().collect(toMap(FieldWeight::getName, identity()));
        graph.edgeSet().forEach(edge -> addWeights(edge, weightsMap, config));
        return graph;
    }

    private void addWeights(SemanticEdge edge, Map<String, FieldWeight> weights, WeightingConfig config) {
        if (isComprehensionEdge(edge)) {
            return;
        }

        float wordCountBoost = 1 + ((edge.getWordCount() - 1) * config.getWordAmountBoostMultiplier());
        edge.getFields().forEach(field -> {
            FieldWeight weight = weights.getOrDefault(field.getName(), new FieldWeight());
            int basicBoost = calculateConceptBoost(field.getType(), weight, config);
            float modifierBoost = edge.getModifiers().stream()
                    .map(SemanticModifier::getRelativeWeight)
                    .reduce(1.0f, (c, m) -> c * m);
            float totalBoost = basicBoost * modifierBoost * wordCountBoost;
            if (totalBoost > 0) {
                field.setBoost(totalBoost);
            } else {
                log.error("Boost calculation failed with total boost value {} for field {}. "
                          + "Basic boost {}, modifier boost {}, word count boost {}. "
                          + "Used weights {}.",
                          totalBoost, field,
                          basicBoost, modifierBoost, wordCountBoost,
                          weight);
            }
        });
    }

    private int calculateConceptBoost(FieldMatchType type, FieldWeight weight, WeightingConfig config) {
        switch (type) {
            case CONCEPT:
                return weight.getConcept() == null ? config.getConceptBoost() : weight.getConcept();
            case SHINGLE:
                return weight.getShingle() == null ? config.getShingleBoost() : weight.getShingle();
            case TEXT:
                return weight.getText() == null ? config.getTextBoost() : weight.getText();
            default:
                return 1;
        }
    }

    private boolean isComprehensionEdge(SemanticEdge edge) {
        return edge.getModifiers().stream().anyMatch(SemanticModifier::isComprehension);
    }
}
