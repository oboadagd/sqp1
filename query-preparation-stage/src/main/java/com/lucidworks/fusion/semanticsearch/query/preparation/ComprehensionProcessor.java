package com.lucidworks.fusion.semanticsearch.query.preparation;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType.CONCEPT;
import static com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType.SHINGLE;
import static com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType.TEXT;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.FILTER;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.SORT;
import static com.lucidworks.fusion.semanticsearch.common.utilities.SemanticEdgeUtils.findOverlaps;
import static java.lang.Integer.parseInt;
import static java.util.Collections.emptySet;

@RequiredArgsConstructor
public class ComprehensionProcessor {

    public OverriddenGraph splitGraphAndComprehensions(SemanticGraph graph,
                                                       boolean skipText,
                                                       boolean skipShingles,
                                                       boolean skipConcepts) {
        Set<SemanticEdge> allEdges = graph.edgeSet();
        Set<SemanticEdge> comprehensions = allEdges.stream()
                .filter(SemanticEdge::containsComprehension)
                //Priority checks inside the edge
                .filter(edge -> skipText || !edge.contains(TEXT))
                .filter(edge -> skipShingles || !edge.contains(SHINGLE))
                .filter(edge -> skipConcepts || !edge.contains(CONCEPT))
                //Priority checks between comprehension and overlapped edges
                .filter(comprehension -> findOverlaps(comprehension, allEdges).stream().noneMatch(overlap -> {
                    boolean containsText = !skipText && overlap.contains(TEXT);
                    boolean containsShingles = !skipShingles && overlap.contains(SHINGLE);
                    boolean containsConcepts = !skipConcepts && overlap.contains(CONCEPT);
                    return containsText || containsShingles || containsConcepts;
                }))
                .collect(Collectors.toSet());
        //No overlaps between comprehensions
        comprehensions.removeAll(comprehensions.stream().flatMap(edge -> {
            Set<SemanticEdge> overlaps = findOverlaps(edge, comprehensions);
            if (overlaps.isEmpty()) {
                return Stream.empty();
            }
            overlaps.add(edge);
            return overlaps.stream();
        }).collect(Collectors.toSet()));

        //Isolated comprehensions are not found
        if (comprehensions.isEmpty()) {
            return new OverriddenGraph(graph, new Comprehensions(emptySet(), emptySet()));
        }

        //Removing of isolated comprehensions from the graph
        SemanticGraph graphOverride = new SemanticGraph();
        List<GraphPath<String, SemanticEdge>> allPaths = graph.getAllPaths();
        allPaths.forEach(path -> {
            List<String> vertexList = path.getVertexList();
            boolean pathMatched = comprehensions.stream()
                    .allMatch(comprehension -> vertexList.contains(comprehension.getSource()) &&
                                               vertexList.contains(comprehension.getTarget()));
            if (!pathMatched) {
                return;
            }

            List<SemanticEdge> pathOverride = path.getEdgeList().stream()
                    .filter(edge -> comprehensions.stream()
                            .noneMatch(comp -> parseInt(edge.getSource()) >= parseInt(comp.getSource()) &&
                                               parseInt(edge.getTarget()) <= parseInt(comp.getTarget())))
                    .collect(Collectors.toList());
            int previousEndVertex = 0;
            for (SemanticEdge edge : pathOverride) {
                int startVertex = Integer.parseInt(edge.getSource());
                int endVertex = Integer.parseInt(edge.getTarget());
                if (startVertex != previousEndVertex) {
                    endVertex -= startVertex - previousEndVertex;
                    startVertex = previousEndVertex;
                }
                String startVertexString = String.valueOf(startVertex);
                String endVertexString = String.valueOf(endVertex);
                SemanticEdge edgeOverride = new SemanticEdge(edge.getToken(),
                                                             Collections.singleton(edge),
                                                             true,
                                                             true);
                graphOverride.addVertex(startVertexString);
                graphOverride.addVertex(endVertexString);
                graphOverride.addEdgeIfAbsent(startVertexString, endVertexString, edgeOverride);
                previousEndVertex = endVertex;
            }
        });

        if (graphOverride.edgeSet().isEmpty()) {
            return new OverriddenGraph(graph, new Comprehensions(emptySet(), emptySet()));
        }
        Set<String> sorting = comprehensions.stream()
                .filter(comprehension -> comprehension.getModifiers().contains(SORT))
                .map(SemanticEdge::getToken)
                .collect(Collectors.toSet());
        Set<String> filters = comprehensions.stream()
                .filter(comprehension -> comprehension.getModifiers().contains(FILTER))
                .map(SemanticEdge::getToken)
                .collect(Collectors.toSet());
        return new OverriddenGraph(graphOverride, new Comprehensions(sorting, filters));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class Comprehensions {

        private final Set<String> sorting;
        private final Set<String> filters;

        public boolean isEmpty() {
            return sorting.isEmpty() && filters.isEmpty();
        }

        public int size() {
            return sorting.size() + filters.size();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static class OverriddenGraph {

        private final SemanticGraph graph;
        private final Comprehensions comprehensions;

    }
}
