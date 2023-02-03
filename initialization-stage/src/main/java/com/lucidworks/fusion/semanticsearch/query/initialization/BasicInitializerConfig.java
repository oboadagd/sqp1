package com.lucidworks.fusion.semanticsearch.query.initialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicInitializerConfig implements SemanticConfig {

    private String productCollection;
    private String fieldType;
}
