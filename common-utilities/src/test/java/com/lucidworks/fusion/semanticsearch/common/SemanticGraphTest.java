package com.lucidworks.fusion.semanticsearch.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SemanticGraphTest {

    @Test
    @SuppressWarnings("java:S2699")
    public void handleRequestBodyProcessSimpleGraph() {
        String inGraph = "digraph G {\n" +
                "  1;\n" +
                "  2;\n" +
                "  3;\n" +
                "  4;\n" +
                "  5;\n" +
                "  6;\n" +
                "  7;\n" +
                "  8;\n" +
                "  9;\n" +
                "  1 -> 2 [ token=\"quick\" color=\"blue\" label=<<b>'quick'</b>> ];\n" +
                "  2 -> 3 [ token=\"red\" color=\"blue\" label=<<b>'red'</b>> ];\n" +
                "  3 -> 4 [ token=\"fox\" color=\"blue\" label=<<b>'fox'</b>> ];\n" +
                "  4 -> 5 [ token=\"jumped\" color=\"blue\" label=<<b>'jumped'</b>> ];\n" +
                "  5 -> 6 [ token=\"over\" color=\"blue\" label=<<b>'over'</b>> ];\n" +
                "  6 -> 7 [ token=\"lazy\" color=\"blue\" label=<<b>'lazy'</b>> ];\n" +
                "  7 -> 8 [ token=\"brown\" color=\"blue\" label=<<b>'brown'</b>> ];\n" +
                "  8 -> 9 [ token=\"dogs\" color=\"blue\" label=<<b>'dogs'</b>> ];\n" +
                "}\n";

        SemanticGraph graph = SemanticGraph.fromDot(inGraph);
        String outGraph = graph.toDot();
        assertEquals(inGraph, outGraph);
    }
}