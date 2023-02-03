package com.lucidworks.fusion.semanticsearch.indexing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.indexing.api.fusion.Fusion;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Analyzer {

    private static final Logger logger = LoggerFactory.getLogger(Analyzer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SOLR_ANALYSIS_URL_TEMPLATE = "http://admin/api/v1/solr/%s/analysis/field";

    private final Fusion fusion;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<String> getTokens(String collectionName, String fieldType, String phrase) {
        String response = analysePhrase(collectionName, fieldType, phrase);
        try {
            Map responseMap = mapper.readValue(response, Map.class);
            Map analysis = (Map) responseMap.get("analysis");
            Map fieldTypes = (Map) analysis.get("field_types");
            Map fieldTypeResult = (Map) fieldTypes.get(fieldType);
            List query = (List) fieldTypeResult.get("query");
            if (CollectionUtils.isEmpty(query)) {
                logger.warn("Empty token list was produced for the phrase: {} ,\n"
                            + "for field type: {} ,\n"
                            + "in the collection: {} .",
                            phrase, fieldType, collectionName);
                return Collections.emptyList();
            }
            return ((List<Object>) query.get(query.size() - 1))
                    .stream()
                    .map(tokenObject -> {
                        Map tokenMap = (Map) tokenObject;
                        return (String) tokenMap.get("text");
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Analysis response parsing failed: " + response, e);
            return Collections.emptyList();
        }
    }

    public String analysePhrase(String collectionName, String fieldType, String phrase) {
        String url = String.format(SOLR_ANALYSIS_URL_TEMPLATE, collectionName);
        logger.info("Analysis request: {} ,\n"
                    + "for the phrase: {} .",
                    phrase, url);
        String response = fusion.restCall(String.class)
                .get(url)
                .param("q", phrase)
                .param("analysis.fieldtype", fieldType)
                .param("wt", "json")
                .execute();
        logger.info("Analysis response: {} ,\n"
                    + "for the value: {} .", response, phrase);
        return response;
    }

}
