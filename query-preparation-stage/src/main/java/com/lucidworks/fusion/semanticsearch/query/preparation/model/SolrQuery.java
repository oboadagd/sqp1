package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.Getter;
import lombok.Setter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class SolrQuery {

    private final Query mainQuery;
    private Set<String> fl = new HashSet<>();
    private Set<String> facetFields = new HashSet<>();
    private boolean debug = false;
    private int rows = 10;
    private int start = 0;
    private Set<String> sort = new HashSet<>();
    private Set<String> fq = new HashSet<>();

    public SolrQuery(Query mainQuery) {
        this.mainQuery = mainQuery;
    }

    public Map<String, String> toMap() {
        HashMap<String, String> solrQuery = new LinkedHashMap<>();
        if (!fl.isEmpty()) {
            solrQuery.put("fl", String.join(",", fl));
        }
        if (!facetFields.isEmpty()) {
            solrQuery.put("facet", "true");
            //TODO: use multimap
            facetFields.forEach(field -> solrQuery.put("facet.field", field));
        }
        if (debug) {
            solrQuery.put("debug", String.valueOf(debug));
        }
        if (rows != 10) {
            solrQuery.put("rows", String.valueOf(rows));
        }
        if (start != 0) {
            solrQuery.put("start", String.valueOf(start));
        }
        if (!fq.isEmpty()) {
            solrQuery.put("fq", String.join(QueryOperator.AND.getQueryRepresentation(), fq));
        }
        if (!sort.isEmpty()) {
            solrQuery.put("sort",
                          "score DESC, " + String.join(", ", sort));
        }
        solrQuery.put("q", mainQuery.toString());
        mainQuery.getLocalParams()
                .forEach(param -> solrQuery.put(param.getName(), param.getValue()));
        return solrQuery;
    }

    public String toCurl(String baseUrl) {
        Map<String, String> params = toMap();
        String body = params.entrySet()
                .stream()
                .map(param -> String.format("%s=%s",
                                            URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8),
                                            URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&\n"));
        return String.format("curl --location --request POST %s -d '\n%s\n'", baseUrl, body)
                .replace("+", "%20");
    }

    @Override
    public String toString() {
        Map<String, String> params = toMap();
        return params.entrySet()
                .stream()
                .map(param -> String.format("%s=%s", param.getKey(), param.getValue()))
                .collect(Collectors.joining("&\n"));
    }

}
