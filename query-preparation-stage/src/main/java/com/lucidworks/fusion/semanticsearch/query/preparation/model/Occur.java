package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
public enum Occur {
    MUST("+"),
    SHOULD("");

    private final String occur;

    @Override
    public String toString() {
        return occur;
    }
}
