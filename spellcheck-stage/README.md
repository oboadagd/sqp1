# Spellcheck stage

The logic implementation for 
[Semantic Graph Spellcheck Stage](../javascript/SemanticGraphSpellcheck.js).

## Deployment
1. Add the following definitions to managed-schema in the product collection:
    ```
    <!-- Field used by spell checker  -->
    <field indexed="true" multiValued="false" name="words" required="false" stored="false" type="text_general"/>
    <copyField source="*_cpt" dest="words"/>
    ```
   
2. The definition of the request handler should be added to _solrconfig.xml_ in the product collection:
   ```
   <!-- A request handler for the spell checker -->
   <requestHandler name="/sqpspell" class="org.apache.solr.handler.component.SearchHandler">
       <lst name="defaults">
           <str name="spellcheck.dictionary">sqpspelldict</str>
           <str name="spellcheck.count">20</str>
       </lst>
       <arr name="last-components">
           <str>sqpspell</str>
       </arr>
   </requestHandler>

   <searchComponent class="solr.SpellCheckComponent" name="sqpspell">
       <lst name="spellchecker">
           <str name="name">sqpspelldict</str>
           <str name="field">words</str>
           <str name="classname">solr.DirectSolrSpellChecker</str>
           <str name="distanceMeasure">internal</str>
           <str name="accuracy">0</str>
           <str name="maxEdits">2</str>
           <str name="minPrefix">0</str>
           <str name="maxInspections">5</str>
           <str name="minQueryLength">2</str>
           <str name="maxQueryLength">100</str>
           <str name="maxQueryFrequency">0.51</str>
       </lst>
   </searchComponent>

    <requestHandler name="/spellcheckRH" class="com.lucidworks.fusion.semanticsearch.query.SpellcheckStageRequestHandler"/>
   ```
