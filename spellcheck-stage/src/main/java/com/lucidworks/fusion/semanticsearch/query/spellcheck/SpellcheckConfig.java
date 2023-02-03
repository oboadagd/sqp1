package com.lucidworks.fusion.semanticsearch.query.spellcheck;

import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import lombok.Getter;

@Getter
public class SpellcheckConfig implements SemanticConfig {
    String collection;
    String accuracy;

    public SpellcheckConfig(final String collection,
                            final String accuracy) {
        this.collection = collection;
        this.accuracy = accuracy;
    }
}
