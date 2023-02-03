package com.lucidworks.fusion.semanticsearch.query.preparation.stage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExactMatchConfig implements SearchStageConfig {

    private final String conceptFieldSuffix;

}
