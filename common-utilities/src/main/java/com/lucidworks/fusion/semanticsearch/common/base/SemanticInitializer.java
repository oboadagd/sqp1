package com.lucidworks.fusion.semanticsearch.common.base;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;

public interface SemanticInitializer<T extends SemanticConfig> {

    SemanticGraph initialize(T config, String query) throws Exception;
}
