package com.lucidworks.fusion.semanticsearch.query.initialization;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.initialization.model.AnalysisResponse.Token;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GraphBuilder {
    public SemanticGraph build(List<Token> tokenList) {
        SemanticGraph graph = new SemanticGraph();

        for (int i = 0, prevPosition = 0; i < tokenList.size(); i++) {
            graph.addVertex(String.valueOf(i));
            graph.addVertex(String.valueOf(i + 1));
            SemanticEdge se = new SemanticEdge();
            Token token = tokenList.get(i);

            if (token.getPosition() <= prevPosition) {
                log.error("Token position [{}] doesn't match in-list position [{}] for the [{}] token ",
                        token.getPosition(), i+1, token.getText());
                throw new UnexpectedGraphSource("Tokens are reshuffled");
            }

            prevPosition = token.getPosition();
            se.setToken(token.getText());
            graph.addEdge(String.valueOf(i), String.valueOf(i + 1), se);
        }
        return graph;
    }

    public static class UnexpectedGraphSource extends RuntimeException {
        public UnexpectedGraphSource(final String message) {
            super(message);
        }
    }
}
