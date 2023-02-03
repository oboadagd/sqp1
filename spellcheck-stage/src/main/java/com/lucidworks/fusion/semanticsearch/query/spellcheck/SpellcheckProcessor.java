package com.lucidworks.fusion.semanticsearch.query.spellcheck;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier;
import com.lucidworks.fusion.semanticsearch.query.spellcheck.model.SpellcheckResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SpellcheckProcessor implements SemanticProcessor<SpellcheckConfig> {
    public static final String SEQ_DELIMITER = " ";

    @Override
    public SemanticGraph process(
            final RestClient client,
            final SpellcheckConfig config,
            final SemanticGraph graph) throws Exception {

        NavigableMap<Integer, SemanticEdge> queryParts = getTokenSequence(graph);

        String phrase = queryParts.values().stream()
                .map(SemanticEdge::getToken)
                .collect(Collectors.joining(SEQ_DELIMITER));
        Map<String, String> params = new HashMap<>();
        params.put("spellcheck", "true");
        params.put("rows", "0");
        params.put("df", "words");
        params.put("q", phrase);
        params.put("spellcheck.accuracy", config.getAccuracy());

        //TODO: make the endpoint configurable
        SpellcheckResponse response = client.get(config.getCollection() + "/sqpspell",
                                                 params, SpellcheckResponse.class);

        mergeSpellsIntoGraph(graph, queryParts, response);
        return graph;
    }

    private void mergeSpellsIntoGraph(SemanticGraph graph,
                                      NavigableMap<Integer, SemanticEdge> queryParts,
                                      SpellcheckResponse spellcheckResponse) {
        for (SpellcheckResponse.Suggestion suggestion : spellcheckResponse.getSuggestions().values()) {
            int startOffset = suggestion.getStartOffset();
            SemanticEdge parent = queryParts.get(startOffset);
            for (String variant : suggestion.getVariants()) {
                SemanticEdge child = parent.createCopy(variant);
                child.getModifiers().add(SemanticModifier.SPELL);
                graph.addEdge(graph.getEdgeSource(parent), graph.getEdgeTarget(parent), child);
            }
        }
    }

    private NavigableMap<Integer, SemanticEdge> getTokenSequence(SemanticGraph graph) {
        NavigableMap<Integer, SemanticEdge> sequence = new TreeMap<>();
        int currentOffset = 0;
        for (SemanticEdge edge : graph.edgeSet()) {
            sequence.put(currentOffset, edge);
            currentOffset += edge.getToken().length() + SEQ_DELIMITER.length();
        }
        return sequence;
    }
}
