package com.lucidworks.fusion.semanticsearch.query;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AddingEdgesTest {
    private static final String graphString = "strict digraph G {  0;  1;  2;  3;  4;  5;  6;  7;  8;  0 -> 1 [ token=\"quick\" ];  1 -> 2 [ token=\"red\" ];  2 -> 3 [ token=\"fox\" ];  3 -> 4 [ token=\"jumped\" ];  4 -> 5 [ token=\"over\" ];  5 -> 6 [ token=\"lazy\" ];  6 -> 7 [ token=\"brown\" ];  7 -> 8 [ token=\"dogs\" ];}";
    private static final Map<String, String> synonyms = new HashMap<>();

    @Before
    public void init() {
        synonyms.put("quick", "fast");
        synonyms.put("red", "ruby");
        synonyms.put("jumped", "hoped");
        synonyms.put("over", "above");
        synonyms.put("lazy", "shiftless");
        synonyms.put("brown", "chocolate-coloured");
        synonyms.put("dogs", "hounds");
    }

    @Test
    public void shouldAddEdgesFromSynonyms() {
        SemanticGraph graph = SemanticGraph.fromDot(graphString);

        for (GraphPath<String, SemanticEdge> path : graph.getAllPaths()) {
            for (SemanticEdge edge : path.getEdgeList()) {
                if(synonyms.containsKey(edge.getToken())) {
                    String syn = synonyms.get(edge.getToken());
                    SemanticEdge newEdge = new SemanticEdge();
                    newEdge.setToken(syn);
                    graph.addEdge(edge.getSource(), edge.getTarget(), newEdge);
                }
            }
        }

        String enrichedGraph = graph.toDot();
        log.info(enrichedGraph);
        Assert.assertTrue(enrichedGraph.contains("fast"));

        synonyms.forEach((key, value) -> Assert.assertTrue(enrichedGraph.contains(value)));
    }
}
