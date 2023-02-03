package com.lucidworks.fusion.semanticsearch.common.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.GenericSolrRequest;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.apache.solr.cloud.ZkController;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.core.SolrCore;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unchecked")
public class InternalSolrJClient implements RestClient {

    private final SolrCore core;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> postText(final String endpoint, final Map<String, String> params, final String body) throws SolrServerException, IOException {
        ModifiableSolrParams sParams = getAsSolrParams(params);

        GenericSolrRequest request = new GenericSolrRequest(
                SolrRequest.METHOD.POST,
                getSolrPath(endpoint),
                sParams);
        request.setContentWriter(new RequestWriter.StringPayloadContentWriter(body, "text/plain"));

        try (SolrClient client = createSolrClient()) {
            return client.request(request, getSolrCollection(endpoint)).toMap(new LinkedHashMap<>());
        }
    }

    @Override
    public <T> T postText(final String endpoint, final Map<String, String> params, final String body, final Class<T> responseFormat) throws Exception {
        Map<String, Object> resp = this.postText(endpoint, params, body);
        return mapper.convertValue(resp, responseFormat);
    }

    @Override
    public Map<String, Object> get(final String endpoint, final Map<String, String> params) throws SolrServerException, IOException {
        ModifiableSolrParams sParams = getAsSolrParams(params);

        GenericSolrRequest request = new GenericSolrRequest(
                SolrRequest.METHOD.GET,
                getSolrPath(endpoint),
                sParams);

        try (SolrClient client = createSolrClient()) {
            return client.request(request, getSolrCollection(endpoint)).toMap(new LinkedHashMap<>());
        }
    }

    @Override
    public <T> T get(final String endpoint, final Map<String, String> params, final Class<T> responseFormat) throws Exception {
        return mapper.convertValue(this.get(endpoint, params), responseFormat);
    }

    private String getSolrCollection(final String endpoint) {
        if (endpoint.startsWith("/")) {
            return endpoint.substring(1, endpoint.indexOf("/", 1));
        } else {
            return endpoint.substring(0, endpoint.indexOf("/"));
        }
    }

    private String getSolrPath(final String endpoint) {
        return endpoint.substring(endpoint.indexOf("/", 1));
    }

    private ModifiableSolrParams getAsSolrParams(final Map<String, String> params) {
        ModifiableSolrParams sParams = new ModifiableSolrParams();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sParams.add(entry.getKey(), entry.getValue());
        }
        return sParams;
    }

    private HttpClient getHttpClient() {
        PoolingHttpClientConnectionManager cm;
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(50);
        return HttpClients.custom().setConnectionManager(cm)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false)).build();
    }

    private SolrClient createSolrClient() {
        SolrClient client;
        ZkController zkController = core.getCoreContainer().getZkController();
        if (zkController != null) {
            client = new CloudSolrClient.Builder(List.of(zkController.getBaseUrl()))
                    .withHttpClient(getHttpClient())
                    .build();
        } else {
            client = new HttpSolrClient.Builder("http://localhost:8983/solr").withHttpClient(getHttpClient()).build();
        }
        return client;
    }

}
