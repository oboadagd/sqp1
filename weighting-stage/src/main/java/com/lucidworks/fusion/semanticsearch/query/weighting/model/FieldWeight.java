package com.lucidworks.fusion.semanticsearch.query.weighting.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldWeight {
    private String name;
    private Integer concept;
    private Integer shingle;
    private Integer text;

    @Override
    public String toString() {
        return String.format("%s:%d:%d:%d", name, concept, shingle, text);
    }

}
