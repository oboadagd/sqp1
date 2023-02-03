package com.lucidworks.fusion.semanticsearch.indexing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.indexing.model.Concept;
import com.lucidworks.indexing.api.fusion.Fusion;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SolrIndexer {

    private static final Logger logger = LoggerFactory.getLogger(SolrIndexer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SOLR_UPDATE_URL_TEMPLATE = "http://admin/api/v1/solr/%s/%s/json";

    private final Fusion fusion;

    public void indexConcepts(String collectionName, List<Concept> concepts) {
        try {
            String url = String.format(SOLR_UPDATE_URL_TEMPLATE, collectionName, "update");
            //TODO: move to RH's defaults
            Map<String, String> updateParams = new HashMap<>();
            updateParams.put("update.chain", "dedupe");
            updateParams.put("commit", "false");
            String conceptDocs = mapper.writeValueAsString(concepts);
            logger.info("Update request: {} ,\n"
                        + "update parameters: {} ,\n"
                        + "concepts: {} .",
                        url, updateParams, conceptDocs);
            String result = fusion.restCall(String.class)
                    .post(url)
                    .header("Content-type", "application/json")
                    .params(updateParams)
                    .param("wt", "json")
                    .body(conceptDocs)
                    .execute();
            logger.info("Update request complete: {}", result);
        } catch (JsonProcessingException e) {
            logger.error("Concept list serialization failed: " + concepts.toString(), e);
        }
    }

}
