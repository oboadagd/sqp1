package com.lucidworks.fusion.semanticsearch.query.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Comprehension {

    private String id;
    private String key;
    private String field;
    private List<String> patterns;
    private ActionType type;
    private ActionOperator operator;
    private SortingOrder order;
}
