package com.lucidworks.fusion.semanticsearch.indexing.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@Getter
@ToString
@Value(staticConstructor = "of")
@EqualsAndHashCode
public class Concept {

    String fname;
    String value;
    ConceptType type;

}
