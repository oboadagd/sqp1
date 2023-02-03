package com.lucidworks.fusion.semanticsearch.query.comprehension;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier;
import com.lucidworks.fusion.semanticsearch.query.model.ActionType;
import com.lucidworks.fusion.semanticsearch.query.model.Comprehension;
import org.apache.commons.lang.math.NumberUtils;
import org.jgrapht.alg.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class ComprehensionCalculator {

    private static final String FILTER_FROM_DEFAULT = "0";
    private static final String FILTER_TO_DEFAULT = "*";
    private static final String SORT_TEMPLATE = "%s %s";
    private static final String FILTER_TEMPLATE = "%s:[%s TO %s]";

    public SemanticGraph calculateComprehension(SemanticGraph graph, List<Comprehension> comprehensions) {
        comprehensions.forEach(comprehension -> {

            List<List<SemanticEdge>> edgeSequences = getEdgeSequences(graph, comprehension);

            edgeSequences.forEach(edgeSequence -> {
                String startVertex = graph.getEdgeSource(edgeSequence.get(0));
                String endVertex = graph.getEdgeTarget(edgeSequence.get(edgeSequence.size()-1));
                String token = createComprehensionToken(edgeSequence, comprehension);

                if (isEdgeNotExist(startVertex, endVertex, token, graph, comprehension.getType())) {
                    graph.addEdge(startVertex, endVertex, createComprehensionEdge(token, comprehension, edgeSequence));
                }
            });
        });

        return graph;
    }

    private List<List<SemanticEdge>> getEdgeSequences(SemanticGraph graph, Comprehension comprehension) {
        if (comprehension.getPatterns() == null) {
            return getEdgeSequencesByKey(graph, comprehension);
        }

        List<List<SemanticEdge>> edgeSequences = new ArrayList<>();
        List<String> patterns = comprehension.getPatterns();

        for (int i = 0; i < patterns.size(); i++) {
            int index = i;
            if (i == 0) {
                Set<SemanticEdge> edgesMatchPattern = graph.edgeSet().stream()
                        .filter(edge -> edge.getToken().matches(patterns.get(index)))
                        .collect(toSet());

                Map<Pair<String, String>, SemanticEdge> vertexPairEdgeMap = new HashMap<>();
                edgesMatchPattern.forEach(edge -> {
                    Pair<String, String> vertexPair = new Pair<>(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
                    vertexPairEdgeMap.putIfAbsent(vertexPair, edge);
                });

                vertexPairEdgeMap.values().forEach(edge -> {
                    List<SemanticEdge> edgeSequence = new ArrayList<>();
                    edgeSequence.add(edge);
                    edgeSequences.add(edgeSequence);
                });
                continue;
            }

            List<List<SemanticEdge>> newSequences = new ArrayList<>();
            Iterator<List<SemanticEdge>> iterator = edgeSequences.iterator();
            while (iterator.hasNext()) {
                List<SemanticEdge> edgeSequence = iterator.next();
                SemanticEdge lastEdge = edgeSequence.get(edgeSequence.size()-1);
                Set<SemanticEdge> edgesMatchPattern = graph.outgoingEdgesOf(graph.getEdgeTarget(lastEdge)).stream()
                        .filter(edge -> edge.getToken().matches(patterns.get(index)))
                        .collect(toSet());

                Map<String, SemanticEdge> targetVertexEdgeMap = new HashMap<>();
                edgesMatchPattern.forEach(edge -> targetVertexEdgeMap.putIfAbsent(graph.getEdgeTarget(edge), edge));

                if (targetVertexEdgeMap.isEmpty()) {
                    iterator.remove();
                } else {
                    List<SemanticEdge> foundEdges = new ArrayList<>(targetVertexEdgeMap.values());
                    SemanticEdge firstFound = foundEdges.get(0);
                    foundEdges.stream().skip(1).forEach(edge -> {
                        List<SemanticEdge> copiedEdgeSequence = new ArrayList<>(edgeSequence);
                        copiedEdgeSequence.add(edge);
                        newSequences.add(copiedEdgeSequence);
                    });
                    edgeSequence.add(firstFound);
                }
            }

            edgeSequences.addAll(newSequences);
        }
        return edgeSequences;
    }

    private List<List<SemanticEdge>> getEdgeSequencesByKey(SemanticGraph graph, Comprehension comprehension) {
        return graph.edgeSet().stream()
                .filter(edge -> edge.getToken().equals(comprehension.getKey()))
                .map(edge -> {
                    List<SemanticEdge> edgeSequence = new ArrayList<>();
                    edgeSequence.add(edge);
                    return edgeSequence; })
                .collect(toList());
    }

    private String createComprehensionToken(List<SemanticEdge> edgeSequence, Comprehension comprehension) {
        List<String> edgeTokens = edgeSequence.stream().map(SemanticEdge::getToken).collect(toList());
        String query = null;

        switch (comprehension.getType()) {
            case SORT: query = String.format(SORT_TEMPLATE, comprehension.getField(), comprehension.getOrder().name());
                break;
            case FILTER: query = createFilterQuery(edgeTokens, comprehension);
        }

        return query;
    }

    private String createFilterQuery(List<String> edgeTokens, Comprehension comprehension) {
        String from = FILTER_FROM_DEFAULT;
        String to = FILTER_TO_DEFAULT;

        List<Integer> values = new ArrayList<>();

        for(String token: edgeTokens) {
            if (NumberUtils.isNumber(token)) {
                values.add(Integer.parseInt(token));
            }
        }

        Collections.sort(values);

        switch (comprehension.getOperator()) {
            case LESS: to = Integer.toString(values.get(0));
                break;
            case MORE: from = Integer.toString(values.get(0));
                break;
            case BETWEEN: {
                from = Integer.toString(values.get(0));
                to = Integer.toString(values.get(1));
            }
        }

        return String.format(FILTER_TEMPLATE, comprehension.getField(), from, to);
    }

    private boolean isEdgeNotExist(String startVertex, String endVertex, String token, SemanticGraph graph, ActionType type) {
        Set<SemanticEdge> edges = graph.getAllEdges(startVertex, endVertex);
        return edges.stream().noneMatch(edge
                -> edge.getToken().equals(token) && edge.getModifiers().contains(SemanticModifier.valueOf(type.name())));
    }

    private SemanticEdge createComprehensionEdge(String token, Comprehension comprehension, List<SemanticEdge> parents) {
        SemanticEdge edge = new SemanticEdge(token, parents, true, false);
        edge.getModifiers().add(SemanticModifier.valueOf(comprehension.getType().name()));
        edge.getComprehensionIds().add(comprehension.getId());
        return edge;
    }
}
