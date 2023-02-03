package com.lucidworks.fusion.semanticsearch.query.graph;

import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.model.Token;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.List;

public class GraphBuilder {
    public DefaultDirectedGraph<String, SemanticEdge> build(List<Token> tokenList) {
        DefaultDirectedGraph<String, SemanticEdge> graph = new DefaultDirectedGraph<>(SemanticEdge.class);

        for (int i = 0; i < tokenList.size(); i++) {
            graph.addVertex(String.valueOf(i));
            graph.addVertex(String.valueOf(i + 1));
            SemanticEdge se = new SemanticEdge();
            se.setToken(tokenList.get(i).getTerm());
            //se.setTagType(SemanticEdgeType.CONCEPT);
            graph.addEdge(String.valueOf(i), String.valueOf(i + 1), se);
        }
        return graph;
    }
}