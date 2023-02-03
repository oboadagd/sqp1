package com.lucidworks.fusion.semanticsearch.indexing.stages;

import com.lucidworks.fusion.semanticsearch.indexing.model.Concept;
import com.lucidworks.fusion.semanticsearch.indexing.model.FusionDocumentField;
import com.lucidworks.fusion.semanticsearch.indexing.service.Analyzer;
import com.lucidworks.fusion.semanticsearch.indexing.service.SolrIndexer;
import com.lucidworks.indexing.api.Context;
import com.lucidworks.indexing.api.Document;
import com.lucidworks.indexing.api.IndexStageBase;
import com.lucidworks.indexing.api.Stage;
import com.lucidworks.indexing.api.fusion.Fusion;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.lucidworks.fusion.semanticsearch.indexing.model.ConceptType.CONCEPT;
import static com.lucidworks.fusion.semanticsearch.indexing.model.ConceptType.SHINGLE;
import static com.lucidworks.fusion.semanticsearch.indexing.model.ConceptType.TEXT;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Stage(type = "concept-indexing", configClass = ConceptIndexingStageConfig.class)
public class ConceptIndexingStage extends IndexStageBase<ConceptIndexingStageConfig> {

    private static final Logger logger = LoggerFactory.getLogger(ConceptIndexingStage.class);

    private static final String CONCEPT_COLLECTION_NAME_TEMPLATE = "%s_concept";
    private static final int MIN_TEXT_TOKENS = 2;
    private static final int MAXIMUM_FIELD_VALUE = 20_000;

    private Analyzer analyzer;
    private SolrIndexer indexer;

    private int minShingleLength;
    private int maxShingleLength;

    @Override
    public void init(ConceptIndexingStageConfig config, Fusion fusion) {
        super.init(config, fusion);
        analyzer = new Analyzer(fusion);
        indexer = new SolrIndexer(fusion);
        String[] shingleLength = config.shingleLength().split("-");
        minShingleLength = Integer.parseInt(shingleLength[0]);
        maxShingleLength = Integer.parseInt(shingleLength[1]);
    }

    @Override
    public Document process(Document product, Context context) {
        try {
            String conceptCollectionName = String.format(CONCEPT_COLLECTION_NAME_TEMPLATE, context.getCollection());
            List<Concept> concepts = product.allFields()
                    .stream()
                    .filter(field -> !field.getName().startsWith("_lw_"))
                    .filter(field -> !config.ignoreFields().contains(field.getName()))
                    .flatMap(field -> field.get().stream()
                            .map(value -> FusionDocumentField.of(field.getName(), value.toString())))
                    .filter(field -> StringUtils.isNotEmpty(field.getValue()))
                    .flatMap(field -> {
                        List<String> tokens = analyzer.getTokens(context.getCollection(),
                                                                 config.analyzerFieldType(),
                                                                 field.getValue());
                        if (CollectionUtils.isEmpty(tokens)) {
                            return Stream.empty();
                        }

                        Set<Concept> conceptSet = new HashSet<>();
                        Concept concept = Concept.of(field.getName(), join(" ", tokens), CONCEPT);

                        if (tokens.size() <= config.maxConceptTokens()) {
                            conceptSet.add(concept);
                        }

                        Set<Concept> shingles = new HashSet<>();
                        if (tokens.size() >= config.minShingleTokens() && field.getValue().length() <= MAXIMUM_FIELD_VALUE) {
                            shingles = sliceShingles(field.getName(), tokens,
                                                                  minShingleLength, maxShingleLength);
                            logger.info("{} shingles produced for concept: {}", shingles.size(), concept.getValue());
                        }

                        Set<Concept> textConcepts = new HashSet<>();
                        if (tokens.size() >= MIN_TEXT_TOKENS) {
                            textConcepts = tokens.stream()
                                    .map(token -> Concept.of(field.getName(), token, TEXT))
                                    .collect(toSet());
                        }

                        return Stream.of(conceptSet, shingles, textConcepts).flatMap(Collection::stream);
                    })
                    .collect(toList());
            indexer.indexConcepts(conceptCollectionName, concepts);
        } catch (Exception e) {
            logger.error("Indexing failed: " + e.getMessage(), e);
        }
        return product;
    }

    private Set<Concept> sliceShingles(String fieldName, List<String> tokens,
                                       int minShingleLength, int maxShingleLength) {
        Set<Concept> shingles = new HashSet<>();
        maxShingleLength = Math.min(tokens.size(), maxShingleLength);
        if (minShingleLength >= maxShingleLength) {
            return shingles;
        }
        for (int i = 0; i < tokens.size() - 1; i++) {
            for (int j = minShingleLength; j <= maxShingleLength && i + j <= tokens.size(); j++) {
                Concept shingle = Concept.of(fieldName, join(" ", tokens.subList(i, i + j)), SHINGLE);
                logger.info("Produced shingle: {}", shingle);
                shingles.add(shingle);
            }
        }
        return shingles;
    }
}
