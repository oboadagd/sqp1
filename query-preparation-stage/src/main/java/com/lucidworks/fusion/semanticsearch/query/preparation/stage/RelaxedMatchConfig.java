package com.lucidworks.fusion.semanticsearch.query.preparation.stage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RelaxedMatchConfig implements SearchStageConfig {

    private final String conceptFieldSuffix;
    private final boolean linguisticsAllowed;
    private final boolean spellcheckAllowed;
    private final boolean considerTypePriority;

}
