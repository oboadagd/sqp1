package com.lucidworks.fusion.semanticsearch.common.base;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;

import java.util.Map;

public interface SemanticTransformer<T extends SemanticConfig> {

    Map<String, String> transform(T config, SemanticGraph graph);
}
