package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.Getter;

import java.math.BigInteger;
import java.util.Set;

public class BooleanClause extends Query {

    private static final String CLAUSE_FORMAT = "%s(%s)";

    private final Query query;
    private final Occur occur;
    @Getter
    private final BigInteger id;

    public BooleanClause(Query query, Occur occur) {
        this.query = query;
        this.occur = occur;
        this.id = new BigInteger(1, toString().getBytes());
    }

    @Override
    public Set<LocalParam> getLocalParams() {
        return query.getLocalParams();
    }

    @Override
    public String toString() {
        return String.format(CLAUSE_FORMAT, occur, query);
    }

}
