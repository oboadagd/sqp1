package com.lucidworks.fusion.semanticsearch.common.client;

import java.util.Map;

public interface RestClient {

    Map<String, Object> postText(String endpoint, Map<String, String> params, String body)
            throws Exception;

    <T> T postText(String endpoint, Map<String, String> params, String body, Class<T> responseFormat)
            throws Exception;

    Map<String, Object> get(String endpoint, Map<String, String> params)
            throws Exception;

    <T> T get(String endpoint, Map<String, String> params, Class<T> responseFormat)
            throws Exception;
}
