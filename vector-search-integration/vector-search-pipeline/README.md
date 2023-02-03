# VectorSearchPipeline stages

## Structure

VectorSearchPipeline consists of the following stages:
* RestoreSearchPhrase - restores the original search phrase from the context.
* SetRows100 - sets Solr query `rows`-parameter to `100`.
* Solr Query - retrieves 100 products with scores from the catalog.
* SetRows10 - returns Solr query `rows`-parameter to `10`.
* VectorSearchMock - mimics Vector Search logic by constructing similar query using product IDs and scores (normalised from 0 to 1) values retrieved from Solr.

## Configuration

### RestoreSearchPhrase

* Fusion query stage type - `JavaScript`
* Label - `RestoreSearchPhrase`
* Script Body - [RestoreSearchPhrase](../never-null-query-pipeline/RestoreSearchPhrase.js)

### SetRows100

* Fusion query stage type - `JavaScript`
* Label - `SetRows100`
* Script Body - [SetRows100](SetRows100.js)

### Solr Query

* Fusion query stage type - `Solr Query`

### SetRows10

* Fusion query stage type - `JavaScript`
* Label - `SetRows10`
* Script Body - [SetRows10](SetRows10.js)

### VectorSearchMock

* Fusion query stage type - `JavaScript`
* Label - `VectorSearchMock`
* Script Body - [VectorSearchMock](VectorSearchMock.js)
