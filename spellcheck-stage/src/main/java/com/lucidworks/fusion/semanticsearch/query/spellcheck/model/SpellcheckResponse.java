package com.lucidworks.fusion.semanticsearch.query.spellcheck.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class SpellcheckResponse {
    @JsonIgnore
    private Map<String, Suggestion> suggestions = Map.of();

    @JsonProperty("spellcheck")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private void unpackSuggestions(Map<String, Map<String, Suggestion>> spellcheck) {
        suggestions = spellcheck.get("suggestions");
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    public static class Suggestion {
        private Integer startOffset;
        @JsonProperty("suggestion")
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<String> variants;
    }

}
