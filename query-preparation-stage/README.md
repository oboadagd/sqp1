# Conceptualization stage

The logic implementation for 
[Semantic Query Preparation Stage](../javascript/SemanticQueryPreparation.js).

## Deployment
1. The definition of the request handler should be added to _solrconfig.xml_:
    ```
    <!-- A request handler for the Solr query preparation query stage  -->
    <requestHandler name="/query-preparation" class="com.lucidworks.fusion.semanticsearch.query.SolrQueryPreparationStageRequestHandler"/>
    ```
