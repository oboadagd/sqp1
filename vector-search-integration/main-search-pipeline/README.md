# MainSearchPipeline stages

## Structure

MainSearchPipeline consists of the following stages:
* SaveSearchPhrase - saves the original search phrase to the pipeline's context.
* RewriteQueryToZRQ - rewrites original search phrase with empty query. 
* Solr Query - mimics zero results response for rewritten query.

## Configuration

### SaveSearchPhrase

* Fusion query stage type - `JavaScript`
* Label - `SaveSearchPhrase`
* Script Body - [SaveSearchPhrase](SaveSearchPhrase.js)

### RewriteQueryToZRQ

* Fusion query stage type - `JavaScript`
* Label - `RewriteQueryToZRQ`
* Script Body - [RewriteQueryToZRQ](RewriteQueryToZRQ.js)

### Solr Query

* Fusion query stage type - `Solr Query`
