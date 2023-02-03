package com.griddynamics.solr.plugin.graph;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.initialization.GraphBuilder;
import com.lucidworks.fusion.semanticsearch.query.initialization.model.AnalysisResponse.Token;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GraphBuilderTest {

    private static final List<String> WORDS = Arrays.asList("the", "quick", "red", "fox", "jumped", "over", "the",
            "lazy", "brown", "dogs");

    private final GraphBuilder tested = new GraphBuilder();

    @Test
    public void shouldCreateGraphFromTokenList() {
        List<Token> tokens = createTokenList();
        String expected = createGraph().toDot();
        String actual = tested.build(tokens).toDot();
        assertEquals(expected, actual);
    }

    @Test(expected = GraphBuilder.UnexpectedGraphSource.class)
    public void shouldThrowExceptionWithShuffledTokenList() {
        List<Token> tokens = createShuffledTokenList();
        tested.build(tokens);
    }

    private SemanticGraph createGraph() {
        SemanticGraph graph = new SemanticGraph();

        for (int i = 0; i < WORDS.size(); i++) {
            String startVertex = String.valueOf(i);
            String endVertex = String.valueOf(i+1);
            SemanticEdge semanticEdge = new SemanticEdge(WORDS.get(i));

            graph.addVertex(startVertex);
            graph.addVertex(endVertex);
            graph.addEdge(startVertex, endVertex, semanticEdge);
        }

        return graph;
    }

    private List<Token> createShuffledTokenList() {
        List<Token> tokens = createTokenList();
        Token token = tokens.get(1);
        tokens.set(1, tokens.get(8));
        tokens.set(8, token);
        return tokens;
    }

    private List<Token> createTokenList() {
        List<Token> tokens = new ArrayList<>();

        for (int i = 0; i < WORDS.size(); i++) {
            Token token = createToken(WORDS.get(i), i+1);
            tokens.add(token);
        }

        return tokens;
    }

    private Token createToken(String text, int position) {
        Token token = new Token();
        token.setText(text);
        token.setPosition(position);
        return token;
    }
}