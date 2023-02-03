package com.lucidworks.fusion.semanticsearch.common.base;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;

public interface SemanticProcessor<T extends SemanticConfig> {

    SemanticGraph process(RestClient client, T config, SemanticGraph graph) throws Exception;
}

