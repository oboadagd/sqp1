package com.lucidworks.fusion.semanticsearch.query.preparation.stage;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Term;
import org.jgrapht.GraphPath;

import java.util.List;

public interface SearchStage {

    List<GraphPath<String, SemanticEdge>> extractAlternativePaths(SemanticGraph graph);

    List<Term> createTerms(SemanticEdge edge);
}
