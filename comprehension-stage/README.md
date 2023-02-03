# Comprehension stage

The logic implementation for
[Comprehension Stage](../javascript/SemanticGraphComprehension.js).

## Deployment
1. Create a new _Collection_ in your Fusion application. It should follow
   the requirements as below:
   * Name pattern is `<application name>_comprehension`
   * Should be placed in the same Solr cluster

2. Add the following definitions to (managed-schema) in the comprehension collection:
     ```
     <field indexed="true" multiValued="false" name="key" required="true" stored="true" type="string"/>
     <field indexed="true" multiValued="false" name="field" required="true" stored="true" type="string"/>
     <field indexed="true" multiValued="true" name="patterns" required="false" stored="true" type="string"/>
     <field indexed="true" multiValued="false" name="type" required="true" stored="true" type="string"/>
     <field indexed="true" multiValued="false" name="operator" required="false" stored="true" type="string"/>
     <field indexed="true" multiValued="false" name="order" required="false" stored="true" type="string"/>
     ```

3. The definition of the request handler should be added to _solrconfig.xml_ in the product collection:
    ```
    <!-- A request handler for the comprehension query stage  -->
    <requestHandler name="/comprehension" class="com.lucidworks.fusion.semanticsearch.query.ComprehensionStageRequestHandler"/>
    ```

4. Index comprehensions, e.g. using [comprehensions.json](../best-buy-catalog/comprehensions.json).
