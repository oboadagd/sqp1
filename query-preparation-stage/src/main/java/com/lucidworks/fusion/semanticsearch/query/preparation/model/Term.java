package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Term {

    private final String fieldName;
    private final String token;
    private final float boost;

}
