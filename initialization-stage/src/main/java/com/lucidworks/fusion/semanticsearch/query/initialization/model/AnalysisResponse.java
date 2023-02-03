package com.lucidworks.fusion.semanticsearch.query.initialization.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysisResponse {
    @JsonIgnore
    Map<String, List<Token>> finalTokens;

    @JsonProperty("analysis")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private void unpackAnalysis(Analysis analysis) {
        this.finalTokens = analysis.fieldTypes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().query.values().stream()
                                // *get last element*
                                .reduce((first, second) -> second)
                                .orElse(List.of())
                ));
    }

    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Analysis {
        @JsonProperty("field_types")
        AnalysisEntry fieldTypes;
        @JsonProperty("field_names")
        AnalysisEntry fieldNames;
    }

    public static class AnalysisEntry extends LinkedHashMap<String, AnalysisSubEntry> {
    }

    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalysisSubEntry {
        LinkedHashMap<String, List<Token>> query;
    }

    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Token {
        private String text;
        private String type;
        private int position;
    }
}
