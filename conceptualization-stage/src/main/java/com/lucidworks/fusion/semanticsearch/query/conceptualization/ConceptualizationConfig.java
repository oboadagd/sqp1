package com.lucidworks.fusion.semanticsearch.query.conceptualization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ConceptualizationConfig implements SemanticConfig {

    private String concepts;
    private String endpoint;
}
