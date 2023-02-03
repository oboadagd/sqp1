package com.lucidworks.fusion.semanticsearch.indexing.stages;

import com.lucidworks.fusion.schema.SchemaAnnotations.ArraySchema;
import com.lucidworks.fusion.schema.SchemaAnnotations.NumberSchema;
import com.lucidworks.fusion.schema.SchemaAnnotations.Property;
import com.lucidworks.fusion.schema.SchemaAnnotations.RootSchema;
import com.lucidworks.fusion.schema.SchemaAnnotations.StringSchema;
import com.lucidworks.indexing.config.IndexStageConfig;

import java.util.List;

@RootSchema(title = "Concept Index Stage",
            description = "Concept Index Stage")
public interface ConceptIndexingStageConfig extends IndexStageConfig {

    @Property(title = "Ignore fields",
              description = "Product fields to be ignored for concept indexing",
              required = false)
    @ArraySchema
    List<String> ignoreFields();

    @Property(title = "Shingle length",
              description = "Shingle length",
              required = true)
    @StringSchema(defaultValue = "2-3",
                  allowedValues = {"2-3", "3-4", "2-4"})
    String shingleLength();

    @Property(title = "Analysis field type",
              description = "Field type to be used to concepts analysis",
              required = true)
    @StringSchema(defaultValue = "text_general")
    String analyzerFieldType();

    @Property(title = "Maximum concept tokens",
              description = "Maximum quantity of tokens to consider the input as a concept",
              required = true)
    @NumberSchema(minimum = 1, defaultValue = 4)
    Integer maxConceptTokens();

    @Property(title = "Minimum shingle tokens",
              description = "Minimum quantity of tokens to slice the input to shingles",
              required = true)
    @NumberSchema(minimum = 1, defaultValue = 5)
    Integer minShingleTokens();
}
