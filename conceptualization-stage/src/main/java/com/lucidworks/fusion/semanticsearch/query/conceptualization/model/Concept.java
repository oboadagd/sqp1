package com.lucidworks.fusion.semanticsearch.query.conceptualization.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Concept {

    private String fname;
    private String value;
    private FieldMatchType type;
}
