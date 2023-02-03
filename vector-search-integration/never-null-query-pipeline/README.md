# NeverNullQueryPipeline stages

## Structure

NeverNullQueryPipeline consists of the following stages:
* SemanticQueryParsing - builds knowledge graph for the original search phrase.
* VectorSearch - mimics Vector Search logic and returns productId-score pairs.
* BoostingStrategy - calls boosting strategy pipeline for specified name.
* Solr Query - performs Solr query got from a boosting strategy.
* SemanticGraphDebug - optional stage for SQP debug purposes.

## Configuration

### SemanticQueryParsing

* Fusion query stage type - `Call Pipeline`
* Label - `SemanticQueryParsing`
* Query Pipeline to Call - [SemanticQueryParsing](../semantic-query-parsing-pipeline/README.md)

### VectorSearch

* Fusion query stage type - `Call Pipeline`
* Label - `VectorSearch`
* Query Pipeline to Call - [VectorSearch](../vector-search-pipeline/README.md)

### BoostingStrategy

* Fusion query stage type - `Call Pipeline`
* Label - `BoostingStrategy`
* Query Pipeline to Call:
    - [SQPBoostingPipeline](../sqp-boosting-pipeline/README.md)

### Solr Query

* Fusion query stage type - `Solr Query`

### SemanticGraphDebug

* Fusion query stage type - `JavaScript`
* Label - `SemanticGraphDebug`
* Script Body - [SemanticGraphDebug](../../javascript/SemanticGraphDebug.js)
