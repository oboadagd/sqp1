package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class LocalParam {

    private static final String QUERY_PARAMETER_FORMAT = "%s=%s";

    private final String name;
    private final String value;

    @Override
    public String toString() {
        return String.format(QUERY_PARAMETER_FORMAT, name, value);
    }

}
