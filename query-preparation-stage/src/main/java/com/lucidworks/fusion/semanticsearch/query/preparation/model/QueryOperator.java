package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueryOperator {
    AND(" AND "),
    OR (" ");
    private final String queryRepresentation;
}

