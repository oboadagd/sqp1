package com.lucidworks.fusion.semanticsearch.query.model;

public class Token {
    private String term;
    private String type;
    private int position;

    public Token(String term, String type, int position) {
        this.term = term;
        this.type = type;
        this.position = position;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Token{" +
                "term='" + term + '\'' +
                ", type='" + type + '\'' +
                ", position=" + position +
                '}';
    }
}
