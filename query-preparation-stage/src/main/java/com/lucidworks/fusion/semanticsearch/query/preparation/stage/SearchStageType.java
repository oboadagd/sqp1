package com.lucidworks.fusion.semanticsearch.query.preparation.stage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SearchStageType {
    EXACT_MATCH("exactMatch", 0),
    RELAXED_MATCH("relaxedMatch", 1);

    private final String name;
    private final int priority;

    public static SearchStageType fromString(String name) {
        for (SearchStageType stage : SearchStageType.values()) {
            if (stage.getName().equals(name)) {
                return stage;
            }
        }
        throw new RuntimeException(String.format("Undefined search stage: \"%s\".", name));
    }
}
