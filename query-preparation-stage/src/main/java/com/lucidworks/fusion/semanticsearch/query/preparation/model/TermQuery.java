package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode(callSuper = false, exclude = "id")
public class TermQuery extends Query {

    private static final String QUERY_FORMAT = "(%s:\"%s\")^=%.1f";

    private final Term term;
    @Getter
    private final BigInteger id;

    public TermQuery(String fieldName, String token, float boost) {
        this(new Term(fieldName, token, boost));
    }

    public TermQuery(Term term) {
        this.term = term;
        this.id = new BigInteger(1, toString().getBytes());
    }

    @Override
    public Set<LocalParam> getLocalParams() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return String.format(QUERY_FORMAT, term.getFieldName(), term.getToken(), term.getBoost());
    }

}
