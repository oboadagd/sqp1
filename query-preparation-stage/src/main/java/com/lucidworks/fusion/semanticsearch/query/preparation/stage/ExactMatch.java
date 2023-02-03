package com.lucidworks.fusion.semanticsearch.query.preparation.stage;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Term;
import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.stream.Collectors;

import static com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType.CONCEPT;
import static com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils.isModified;

@RequiredArgsConstructor
public class ExactMatch implements SearchStage {

    private final ExactMatchConfig config;

    @Override
    public List<GraphPath<String, SemanticEdge>> extractAlternativePaths(SemanticGraph graph) {
        return graph.getAllPaths().stream()
                //All edges must be recognized
                .filter(GraphPathUtils::isRecognized)
                //All edges must contain concepts
                .filter(GraphPathUtils::isConcept)
                //All modifiers are forbidden
                .filter(path -> !isModified(path))
                .collect(Collectors.toList());
    }

    @Override
    public List<Term> createTerms(SemanticEdge edge) {
        String token = edge.getToken();
        return edge.getFields().stream()
                .filter(field -> CONCEPT.equals(field.getType()))
                .filter(field -> field.getBoost() != null && field.getBoost() > 0)
                .map(field -> new Term(field.getName() + config.getConceptFieldSuffix(),
                                       token, field.getBoost()))
                .collect(Collectors.toList());
    }
}
