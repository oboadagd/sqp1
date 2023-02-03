package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import java.math.BigInteger;
import java.util.Set;

public abstract class Query {

    public abstract Set<LocalParam> getLocalParams();

    public abstract BigInteger getId();

    @Override
    public String toString() {
        String errorMessage = "toString method was not overridden for the class: " + this.getClass().getSimpleName();
        throw new RuntimeException(errorMessage);
    }

}
