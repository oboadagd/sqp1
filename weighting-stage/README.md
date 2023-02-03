# Weighting stage

The logic implementation for
[Weighting Stage](../javascript/SemanticGraphWeighting.js).

## Deployment
1. Create a new _Collection_ in your Fusion application. It should follow
   the requirements as below:
   * Name pattern is `<application name>_weights`
   * Should be placed in the same Solr cluster

2. Add the following definitions to (managed-schema) in the "weights" collection:
     ```
     <field indexed="true" multiValued="false" name="name" required="true" stored="true" type="string"/>
     <field indexed="true" multiValued="false" name="concept" required="false" stored="true" type="pint"/>
     <field indexed="true" multiValued="false" name="shingle" required="false" stored="true" type="pint"/>
     <field indexed="true" multiValued="false" name="text" required="false" stored="true" type="pint"/>
     ```

3. The definition of the request handler should be added to _solrconfig.xml_ in the product collection:
    ```
    <!-- A request handler for the weighting query stage  -->
    <requestHandler name="/weighting" class="com.lucidworks.fusion.semanticsearch.query.WeightingStageRequestHandler"/>
    ```

4. Index weights, e.g. using [weights.json](../best-buy-catalog/weights.json).
