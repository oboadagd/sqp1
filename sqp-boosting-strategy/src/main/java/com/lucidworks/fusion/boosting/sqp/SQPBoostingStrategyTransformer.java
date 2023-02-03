package com.lucidworks.fusion.boosting.sqp;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticTransformer;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.BooleanClause;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.BooleanQuery;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.DisjunctionMaxQuery;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Occur;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Query;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.SolrQuery;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.Term;
import com.lucidworks.fusion.semanticsearch.query.preparation.model.TermQuery;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.GraphPath;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.lucidworks.fusion.semanticsearch.common.utilities.GraphPathUtils.containsComprehension;

public class SQPBoostingStrategyTransformer implements SemanticTransformer<SQPBoostingStrategyConfig> {

    @Override
    public Map<String, String> transform(SQPBoostingStrategyConfig config, SemanticGraph graph) {
        String vectorSearchQuery = config.getVectorSearchQuery();
        if (StringUtils.isEmpty(vectorSearchQuery)) {
            return Collections.emptyMap();
        }
        //TODO: cover ids and scores with unit tests
        String[] vectorSearchClauses = vectorSearchQuery
                .substring(1, vectorSearchQuery.length() - 1)   //removes "(" and ")"
                .split(" OR ");
        Map<String, String> vectorScores = Stream.of(vectorSearchClauses)
                .map(clause -> clause.split("\\^"))
                .filter(clause -> clause.length == 2)
                .collect(Collectors.toMap(clause -> clause[0],      //product ID
                                          clause -> clause[1]));    //score

        Query boostQuery = new BooleanClause(graphToQuery(config, graph), Occur.SHOULD);

        Query vectorQuery = new BooleanClause(buildVectorQuery(vectorScores), Occur.MUST);

        BooleanQuery resultQuery = new BooleanQuery(Arrays.asList(vectorQuery, boostQuery));

        SolrQuery solrQuery = new SolrQuery(resultQuery);
        return solrQuery.toMap();
    }

    public Query buildVectorQuery(Map<String, String> vectorScores) {
        List<Query> termQueries = vectorScores.keySet().stream()
                //TODO: use original boost values instead of 1 in case of SQP boost query is empty
                .map(id -> new TermQuery("id", id, 1))
                .map(query -> new BooleanClause(query, Occur.SHOULD))
                .collect(Collectors.toList());
        return new BooleanQuery(termQueries);
    }

    private Query graphToQuery(SQPBoostingStrategyConfig config, SemanticGraph graph) {
        List<Query> alternativeClauses = graph.getAllPaths().stream()
                .filter(path -> !containsComprehension(path))
                .filter(path -> config.isAllowPartialMatch() || GraphPathUtils.isRecognized(path))
                .map(edge -> pathToQuery(config, edge))
                .collect(Collectors.toList());
        return new DisjunctionMaxQuery(alternativeClauses);
    }

    private Query pathToQuery(SQPBoostingStrategyConfig config, GraphPath<String, SemanticEdge> path) {
        List<Query> pathClauses = path.getEdgeList().stream()
                .filter(edge -> !edge.getFields().isEmpty())
                .map(edge -> edgeToQuery(config, edge))
                .map(query -> new BooleanClause(query, Occur.MUST))
                .collect(Collectors.toList());
        return new BooleanQuery(pathClauses);
    }

    private Query edgeToQuery(SQPBoostingStrategyConfig config, SemanticEdge edge) {
        List<Query> edgeClauses = createTerms(edge).stream()
                .map(TermQuery::new)
                .collect(Collectors.toList());
        return new DisjunctionMaxQuery(edgeClauses);
    }

    public List<Term> createTerms(SemanticEdge edge) {
        String token = edge.getToken();
        return edge.getFields().stream()
                .filter(field -> field.getBoost() != null && field.getBoost() > 0)
                .map(field -> new Term(field.getName() + "_cpt", token, field.getBoost()))
                .collect(Collectors.toList());
    }

}
