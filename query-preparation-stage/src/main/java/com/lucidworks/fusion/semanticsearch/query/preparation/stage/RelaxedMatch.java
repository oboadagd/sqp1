package com.lucidworks.fusion.semanticsearch.query.preparation.stage;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.Field;
import com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Term;
import lombok.RequiredArgsConstructor;
import org.jgrapht.GraphPath;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils.containsComprehension;
import static com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils.containsLinguistics;
import static com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils.containsSpellcheck;

@RequiredArgsConstructor
public class RelaxedMatch implements SearchStage {

    private final RelaxedMatchConfig config;

    @Override
    public List<GraphPath<String, SemanticEdge>> extractAlternativePaths(SemanticGraph graph) {
        List<GraphPath<String, SemanticEdge>> allPaths = graph.getAllPaths().stream()
                //All concept types and modifiers are allowed except comprehensions and restrictions from stage config
                .filter(path -> !containsComprehension(path))
                .filter(path -> config.isLinguisticsAllowed() || !containsLinguistics(path))
                .filter(path -> config.isSpellcheckAllowed() || !containsSpellcheck(path))
                .collect(Collectors.toList());
        List<GraphPath<String, SemanticEdge>> recognizedPaths = allPaths.stream()
                .filter(GraphPathUtils::isRecognized)
                .collect(Collectors.toList());
        return Optional.of(recognizedPaths)
                .filter(paths -> !paths.isEmpty())
                //TODO: avoid duplicates when unrecognized edges will be skipped
                //Unrecognized paths are allowed in case of recognized paths are absent
                .orElse(allPaths);
    }

    @Override
    public List<Term> createTerms(SemanticEdge edge) {
        String token = edge.getToken();
        int minPriority = config.isConsiderTypePriority() ? edge.getFields().stream()
                .map(Field::getType)
                .distinct()
                .max(Comparator.comparing(FieldMatchType::getPriority))
                .orElse(FieldMatchType.TEXT)
                .getPriority() : FieldMatchType.TEXT.getPriority();
        return edge.getFields().stream()
                .filter(field -> minPriority <= field.getType().getPriority())
                .filter(field -> field.getBoost() != null && field.getBoost() > 0)
                .map(field -> new Term(field.getName() + config.getConceptFieldSuffix(),
                                       token, field.getBoost()))
                .collect(Collectors.toList());
    }
}
