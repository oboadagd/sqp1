package com.lucidworks.fusion.semanticsearch.common.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Field {
    private final String name;
    private FieldMatchType type;
    private Float boost;

    @Override
    public String toString() {
        return String.format("%s.%s:%.1f", name, type.getShortName(), boost);
    }

}
