# SemanticQueryParsingPipeline stages

## Structure

SemanticQueryParsingPipeline consists of the following stages:
* RestoreSearchPhrase - restores the original search phrase from the context.
* Initialization - knowledge graph initialization.
* Spellcheck - spell correction.
* Linguistics - linguistic enrichment.
* Conceptualization - concept recognition.
* Weighting - calculates weights for each recognized concept.

## Configuration

### RestoreSearchPhrase

* Fusion query stage type - `JavaScript`
* Label - `RestoreSearchPhrase`
* Script Body - [RestoreSearchPhrase](../never-null-query-pipeline/RestoreSearchPhrase.js)

### Initialization

* Fusion query stage type - `JavaScript`
* Label - `Initialization`
* Script Body - [Initialization](../../javascript/SemanticGraphInitialization.js)

### Spellcheck

* Fusion query stage type - `JavaScript`
* Label - `Spellcheck`
* Script Body - [Spellcheck](../../javascript/SemanticGraphSpellcheck.js)

### Linguistics

* Fusion query stage type - `JavaScript`
* Label - `Linguistics`
* Script Body - [Linguistics](../../javascript/SemanticGraphLinguistic.js)

### Conceptualization

* Fusion query stage type - `JavaScript`
* Label - `Conceptualization`
* Script Body - [Conceptualization](../../javascript/SemanticGraphConceptualization.js)

### Weighting

* Fusion query stage type - `JavaScript`
* Label - `Weighting`
* Script Body - [Weighting](../../javascript/SemanticGraphWeighting.js)
