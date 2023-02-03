package com.lucidworks.fusion.semanticsearch.query.conceptualization;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.Field;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils;
import com.lucidworks.fusion.semanticsearch.query.conceptualization.model.Concept;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

public class ConceptualizationCalculator {

    public SemanticGraph calculate(SemanticGraph graph,
                                   List<GraphPath<String, SemanticEdge>> paths,
                                   List<Concept> concepts) {
        List<Concept> unambiguousConcepts = removeAmbiguousConcepts(concepts);
        Map<String, List<Concept>> valueConceptMap = getValueConceptMap(unambiguousConcepts);
        paths.forEach(path -> calculateConceptsForPath(graph, path, valueConceptMap));
        return graph;
    }

    private List<Concept> removeAmbiguousConcepts(List<Concept> concepts) {
        return new ArrayList<>(concepts.stream()
                .collect(Collectors.toMap(concept -> new Pair<>(concept.getFname(), concept.getValue()), identity(),
                        (first, second) -> first.getType().getPriority() > second.getType().getPriority()
                                ? first : second)).values());
    }

    private Map<String, List<Concept>> getValueConceptMap(List<Concept> concepts) {
        return concepts.stream().collect(Collectors.groupingBy(Concept::getValue));
    }

    private void calculateConceptsForPath(SemanticGraph graph,
                                          GraphPath<String, SemanticEdge> path,
                                          Map<String, List<Concept>> valueConceptMap) {

        String phrase = GraphPathUtils.createPhrase(path);
        List<Concept> concepts = valueConceptMap.get(phrase);

        if (concepts != null) {
            List<Field> fields = concepts.stream()
                    .map(concept -> Field.builder()
                            .name(concept.getFname())
                            .type(concept.getType())
                            .build())
                    .collect(toList());

            SemanticEdge edge = getEdgeFromPath(path, phrase);

            if (path.getEdgeList().size() > 1) {
                graph.addEdge(path.getStartVertex(), path.getEndVertex(), edge);
            }

            edge.getFields().addAll(fields);
        }
    }

    private SemanticEdge getEdgeFromPath(GraphPath<String, SemanticEdge> path, String phrase) {
        return path.getEdgeList().size() == 1
                ? path.getEdgeList().get(0)
                : new SemanticEdge(phrase, path.getEdgeList(), true, false);
    }
}
