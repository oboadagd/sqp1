package com.lucidworks.fusion.semanticsearch.query.comprehension;

import com.lucidworks.fusion.semanticsearch.query.model.ActionOperator;
import com.lucidworks.fusion.semanticsearch.query.model.ActionType;
import com.lucidworks.fusion.semanticsearch.query.model.Comprehension;
import com.lucidworks.fusion.semanticsearch.query.model.SortingOrder;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class ComprehensionMapper {

    private static final String RESPONSE = "response";
    private static final String ID = "id";
    private static final String KEY = "key";
    private static final String FIELD = "field";
    private static final String TYPE = "type";
    private static final String OPERATOR = "operator";
    private static final String ORDER = "order";
    private static final String PATTERNS = "patterns";

    @SuppressWarnings("unchecked")
    public List<Comprehension> mapResponse(Map<String, Object> response) {
        List<Map<String, Object>> docs = (List<Map<String, Object>>) response.get(RESPONSE);
        return docs.stream()
                .map(this::mapDoc)
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private Comprehension mapDoc(Map<String, Object> doc) {
        Comprehension comprehension = new Comprehension();
        comprehension.setId((String) doc.get(ID));
        comprehension.setKey((String) doc.get(KEY));
        comprehension.setField((String) doc.get(FIELD));
        if (doc.get(TYPE) != null) {
            comprehension.setType(ActionType.valueOf(((String) doc.get(TYPE)).toUpperCase()));
        }
        if (doc.get(OPERATOR) != null) {
            comprehension.setOperator(ActionOperator.valueOf(((String) doc.get(OPERATOR)).toUpperCase()));
        }
        if (doc.get(ORDER) != null) {
            comprehension.setOrder(SortingOrder.valueOf(((String) doc.get(ORDER)).toUpperCase()));
        }
        comprehension.setPatterns(((List<String>) doc.get(PATTERNS)));
        return comprehension;
    }
}
