package com.lucidworks.fusion.boosting.sqp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SQPBoostingStrategyConfig implements SemanticConfig {

    private String vectorSearchQuery;
    private boolean allowPartialMatch;
    private String graph;
    private boolean debug;

}
