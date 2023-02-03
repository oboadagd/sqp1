package com.lucidworks.fusion.semanticsearch.query.preparation.stage;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.lucidworks.fusion.semanticsearch.query.preparation.stage.SearchStageType.EXACT_MATCH;
import static com.lucidworks.fusion.semanticsearch.query.preparation.stage.SearchStageType.RELAXED_MATCH;

public class SearchStageFactory {

    private static final String STAGE_NAME = "stageName";
    private static final String CONCEPT_FIELD_SUFFIX = "conceptFieldSuffix";

    private final Map<SearchStageType, Function<Map<String, Object>, SearchStage>> strategies;

    public SearchStageFactory() {
        this.strategies = new HashMap<>();
        strategies.put(EXACT_MATCH, this::exactMatchStage);
        strategies.put(RELAXED_MATCH, this::relaxedMatchStage);
    }

    public SearchStage createSearchStage(Map<String, Object> config) {
        String stageName = (String) config.get(STAGE_NAME);
        if (StringUtils.isEmpty(stageName)) {
            throw new RuntimeException("Stage name is undefined.");
        }
        SearchStageType stageType = SearchStageType.fromString(stageName);
        return strategies.get(stageType).apply(config);
    }

    private SearchStage exactMatchStage(Map<String, Object> config) {
        String conceptFieldSuffix = (String) config.getOrDefault(CONCEPT_FIELD_SUFFIX, "");
        return new ExactMatch(new ExactMatchConfig(conceptFieldSuffix));
    }

    private SearchStage relaxedMatchStage(Map<String, Object> config) {
        String conceptFieldSuffix = (String) config.getOrDefault(CONCEPT_FIELD_SUFFIX, "");
        boolean linguisticsAllowed = Boolean.parseBoolean((String) config.getOrDefault("linguisticsAllowed",
                                                                                       "true"));
        boolean spellcheckAllowed = Boolean.parseBoolean((String) config.getOrDefault("spellcheckAllowed",
                                                                                      "true"));
        boolean considerTypePriority = Boolean.parseBoolean((String) config.getOrDefault("considerTypePriority",
                                                                                         "false"));
        RelaxedMatchConfig stageConfig = new RelaxedMatchConfig(conceptFieldSuffix,
                                                                linguisticsAllowed,
                                                                spellcheckAllowed,
                                                                considerTypePriority);
        return new RelaxedMatch(stageConfig);
    }

}
