package com.lucidworks.fusion.semanticsearch.query.process;

import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import lombok.Data;

@Data
public class LinguisticStageConfig implements SemanticConfig {
    private String synonymCollection;
    private String synonymEndpoint;
    private Integer windowSize;

    public LinguisticStageConfig(final String synonymCollection, final String synonymEndpoint, final Integer windowSize) {
        this.synonymCollection = synonymCollection;
        this.synonymEndpoint = synonymEndpoint;
        this.windowSize = windowSize;
    }
}
