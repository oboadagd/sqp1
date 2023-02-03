package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false, exclude = "id")
public class DisjunctionMaxQuery extends Query {

    private static final String QUERY_FORMAT = "_query_:\"{!maxscore tie=0.0 v=$dismax%s}\"";
    private static final String LOCAL_PARAM_NAME_FORMAT = "dismax%s";
    private static final String LOCAL_PARAM_VALUE_FORMAT = "(%s)";
    private static final String EMPTY_LOCAL_PARAM_VALUE_FORMAT = "%s";
    private static final String CLAUSE_DELIMITER = " | ";

    private final List<Query> subQueries;
    @Getter
    private final BigInteger id;

    public DisjunctionMaxQuery(List<Query> subQueries) {
        this.subQueries = Collections.unmodifiableList(subQueries);
        this.id = new BigInteger(1,
                                 subQueries.stream()
                                         .map(Query::toString)
                                         .collect(Collectors.joining(""))
                                         .getBytes());
    }

    @Override
    public Set<LocalParam> getLocalParams() {
        String dismaxQuery = subQueries.stream()
                .map(Query::toString)
                .filter(query -> !query.isBlank())
                .collect(Collectors.joining(CLAUSE_DELIMITER));
        String localParamValueFormat = dismaxQuery.isBlank() ?
                EMPTY_LOCAL_PARAM_VALUE_FORMAT : LOCAL_PARAM_VALUE_FORMAT;
        LocalParam localQuery = new LocalParam(String.format(LOCAL_PARAM_NAME_FORMAT, id),
                                               String.format(localParamValueFormat, dismaxQuery));
        return Stream.of(Collections.singletonList(localQuery),
                         subQueries.stream()
                                 .map(Query::getLocalParams)
                                 .flatMap(Collection::stream)
                                 .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return String.format(QUERY_FORMAT, id);
    }

}
