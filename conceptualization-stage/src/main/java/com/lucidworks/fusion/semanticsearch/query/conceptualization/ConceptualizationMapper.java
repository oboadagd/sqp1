package com.lucidworks.fusion.semanticsearch.query.conceptualization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.semanticsearch.query.conceptualization.model.Concept;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


public class ConceptualizationMapper {
    private static final String RESPONSE = "response";

    private final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public List<Concept> mapResponse(Map<String, Object> response) {
        List<Map<String, Object>> docs = (List<Map<String, Object>>) response.get(RESPONSE);
        return docs.stream()
                .map(this::mapDoc)
                .collect(toList());
    }

    private Concept mapDoc(Map<String, Object> doc) {
        return mapper.convertValue(doc, Concept.class);
    }
}
