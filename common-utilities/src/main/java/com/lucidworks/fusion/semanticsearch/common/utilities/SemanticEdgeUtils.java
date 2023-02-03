package com.lucidworks.fusion.semanticsearch.common.utilities;

import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SemanticEdgeUtils {

    public static Set<SemanticEdge> findOverlaps(SemanticEdge edge, Collection<SemanticEdge> edges) {
        return edges.stream()
                .filter(e -> !e.equals(edge))
                .filter(e -> Integer.parseInt(edge.getSource()) < Integer.parseInt(e.getTarget()) &&
                             Integer.parseInt(edge.getTarget()) > Integer.parseInt(e.getSource()))
                .collect(Collectors.toSet());
    }

}
