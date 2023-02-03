package com.lucidworks.fusion.semanticsearch.common.utilities;

import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import org.jgrapht.GraphPath;

import java.util.stream.Collectors;

import static com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType.CONCEPT;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.HYP;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.SPELL;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.SYN;

public class GraphPathUtils {

    public static boolean isRecognized(GraphPath<String, SemanticEdge> path) {
        return path.getEdgeList().stream().noneMatch(edge -> edge.getFields().isEmpty());
    }

    public static boolean isConcept(GraphPath<String, SemanticEdge> path) {
        return path.getEdgeList().stream().allMatch(edge -> edge.contains(CONCEPT));
    }

    public static boolean isModified(GraphPath<String, SemanticEdge> path) {
        return path.getEdgeList().stream().anyMatch(edge -> !edge.getModifiers().isEmpty());
    }

    public static boolean containsComprehension(GraphPath<String, SemanticEdge> path) {
        return path.getEdgeList().stream().anyMatch(SemanticEdge::containsComprehension);
    }

    public static boolean containsLinguistics(GraphPath<String, SemanticEdge> path) {
        return path.getEdgeList().stream().anyMatch(edge -> edge.contains(SYN) || edge.contains(HYP));
    }

    public static boolean containsSpellcheck(GraphPath<String, SemanticEdge> path) {
        return path.getEdgeList().stream().anyMatch(edge -> edge.contains(SPELL));
    }

    public static String createPhrase(GraphPath<String, SemanticEdge> path) {
        return path.getEdgeList().stream().map(SemanticEdge::getToken).collect(Collectors.joining(" "));
    }
}
