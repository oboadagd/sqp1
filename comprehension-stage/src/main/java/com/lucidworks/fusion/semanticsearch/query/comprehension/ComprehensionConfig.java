package com.lucidworks.fusion.semanticsearch.query.comprehension;

import com.lucidworks.fusion.semanticsearch.common.base.SemanticConfig;
import lombok.Value;

@Value
public class ComprehensionConfig implements SemanticConfig {
    String dictionary;
    String endpoint;
}
