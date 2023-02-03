package com.lucidworks.fusion.semanticsearch.query.preparation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.SearchStage;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolrQueryPreparationConfig implements SemanticConfig {

    private SearchStage searchStage;
    private boolean allowComprehensions;
    private String graph;
    private boolean debug;

}
