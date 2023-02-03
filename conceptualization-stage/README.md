# Conceptualization stage

On this stage we send request to text tagger component who provides the following 
features: recognition of multi-word tokens, automatic tag dictionary indexation, re-indexation capability.

The logic implementation for 
[Semantic Graph Conceptualization Stage](../javascript/SemanticGraphConceptualization.js).

## Deployment
1. Create a new `Collection` in your Fusion application. It should follow 
the requirements as below:
    * Name pattern is `<application name>_concept`
    * Should be placed in the same Solr cluster
2. Add the following definitions to managed-schema in the concept collection:
    ```
    <!-- Required for the deduplication chain and based on "fname", "value" and "type" fields  -->
    <field indexed="true" multiValued="false" name="signatureField" required="true" stored="false" type="string"/>

    <!-- Concepts  -->
    <field indexed="true" multiValued="false" name="value" required="true" stored="true" type="string"/>
    <field indexed="true" multiValued="false" name="type" required="true" stored="true" type="string"/>
    <field indexed="true" multiValued="false" name="fname" required="true" stored="true" type="string"/>
    ```
3. The definition of the request handler should be added to _solrconfig.xml_ in the product collection:
   ```
    <!-- A request handler for the conceptualization query stage  -->
    <requestHandler name="/conceptualization" class="com.lucidworks.fusion.semanticsearch.query.ConceptualizationStageRequestHandler"/>
   ```
4. Index concepts using [concept indexing stage](../concept-indexing-stage).
