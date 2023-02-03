package com.lucidworks.fusion.semanticsearch.query.preparation;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticTransformer;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.preparation.ComprehensionProcessor.Comprehensions;
import com.lucidworks.fusion.semanticsearch.query.preparation.ComprehensionProcessor.OverriddenGraph;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.BooleanClause;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.BooleanQuery;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.DisjunctionMaxQuery;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Occur;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Query;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.SolrQuery;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.TermQuery;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.SearchStage;
import org.jgrapht.GraphPath;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolrQueryPreparationTransformer implements SemanticTransformer<SolrQueryPreparationConfig> {

    private final ComprehensionProcessor comprehensionProcessor = new ComprehensionProcessor();

    @Override
    public Map<String, String> transform(SolrQueryPreparationConfig config,
                                         SemanticGraph graph) {
        SolrQuery solrQuery;
        if (config.isAllowComprehensions()) {
            OverriddenGraph overriddenGraph = comprehensionProcessor.splitGraphAndComprehensions(graph,
                                                                                                 true,
                                                                                                 true,
                                                                                                 false);
            solrQuery = buildQuery(config.getSearchStage(), overriddenGraph);
        } else {
            solrQuery = buildQuery(config.getSearchStage(), graph);
        }
        return solrQuery.toMap();
    }

    private SolrQuery buildQuery(SearchStage searchStage,
                                 SemanticGraph graph) {
        return new SolrQuery(graphToQuery(searchStage, graph));
    }

    private SolrQuery buildQuery(SearchStage searchStage,
                                 OverriddenGraph graph) {
        SolrQuery solrQuery = buildQuery(searchStage, graph.getGraph());
        Comprehensions comprehensions = graph.getComprehensions();
        solrQuery.setSort(comprehensions.getSorting());
        solrQuery.setFq(comprehensions.getFilters());
        return solrQuery;
    }

    private Query graphToQuery(SearchStage searchStage,
                               SemanticGraph graph) {
        List<Query> alternativeClauses = searchStage.extractAlternativePaths(graph)
                .stream()
                .map(path -> pathToQuery(searchStage, path))
                .collect(Collectors.toList());
        return new DisjunctionMaxQuery(alternativeClauses);
    }

    private Query pathToQuery(SearchStage searchStage,
                              GraphPath<String, SemanticEdge> path) {
        List<Query> pathClauses = path.getEdgeList().stream()
                .filter(edge -> !edge.getFields().isEmpty())
                .map(edge -> edgeToQuery(searchStage, edge))
                .map(query -> new BooleanClause(query, Occur.MUST))
                .collect(Collectors.toList());
        return new BooleanQuery(pathClauses);
    }

    private Query edgeToQuery(SearchStage searchStage,
                              SemanticEdge edge) {
        List<Query> edgeClauses = searchStage.createTerms(edge)
                .stream()
                .map(TermQuery::new)
                .collect(Collectors.toList());
        return new DisjunctionMaxQuery(edgeClauses);
    }

}
