package com.lucidworks.fusion.semanticsearch.indexing.model;

import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@Getter
@ToString
@Value(staticConstructor = "of")
public class FusionDocumentField {

    String name;
    String value;

}
