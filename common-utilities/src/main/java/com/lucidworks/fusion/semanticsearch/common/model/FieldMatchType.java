package com.lucidworks.fusion.semanticsearch.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FieldMatchType {
    CONCEPT ("CONCEPT", 10),
    SHINGLE ("SHINGLE", 5),
    TEXT ("TEXT", 1);

    private final String shortName;
    private final Integer priority;
}
