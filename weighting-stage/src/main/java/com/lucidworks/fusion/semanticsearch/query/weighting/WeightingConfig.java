package com.lucidworks.fusion.semanticsearch.query.weighting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeightingConfig implements SemanticConfig {

    private String weightingCollection;
    private String endpoint;
    private int conceptBoost;
    private int shingleBoost;
    private int textBoost;
    private float wordAmountBoostMultiplier;
}
