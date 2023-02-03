package com.lucidworks.fusion.semanticsearch.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SemanticModifier {
    SORT("SORT", true, 1.0f),
    FILTER("FILTER", true, 1.0f),
    SPELL("SPELL", false, 0.5f),
    SYN("SYN", false, 0.75f),
    HYP("HYP", false, 0.25f);


    private final String shortName;
    private final boolean comprehension;
    private final float relativeWeight;
}
