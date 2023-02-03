package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false, exclude = "id")
public class BooleanQuery extends Query {

    private static final String QUERY_FORMAT = "(%s)";
    private static final String CLAUSE_DELIMITER = " ";
    private static final String EMPTY_QUERY = "";

    private final List<Query> clauses;
    @Getter
    private final BigInteger id;

    public BooleanQuery(List<Query> clauses) {
        this.clauses = clauses;
        this.id = new BigInteger(1,
                                 clauses.stream()
                                         .map(Query::toString)
                                         .collect(Collectors.joining(""))
                                         .getBytes());
    }

    @Override
    public Set<LocalParam> getLocalParams() {
        return clauses.stream()
                .map(Query::getLocalParams)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        String booleanQuery = clauses.stream()
                .map(Query::toString)
                .filter(query -> !query.isBlank())
                .collect(Collectors.joining(CLAUSE_DELIMITER));
        return booleanQuery.isBlank() ? EMPTY_QUERY : String.format(QUERY_FORMAT, booleanQuery);
    }

}
