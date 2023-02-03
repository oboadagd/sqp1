# Initialization stage

On this stage we get request and creating tokens from it: convert capital letters to
lowercase, deleting punctuation, special symbols and creating graph with edges - tokens.

The logic implementation for
[Semantic Graph Initialization Stage](../javascript/SemanticGraphInitialization.js).

## Deployment
1. Add `overridenstemdict.txt` file to a product collection folder.
2. Update `stopwords.txt` file with stop words in a product collection folder - optional.
3. The following fields should be defined in order to perform search analysis:
   ```
   <!-- Common field type used for the same analysis in query and index time  -->
   <fieldType name="joined_text"
              class="solr.TextField"
              stored="false"
              indexed="true"
              omitNorms="true"
              docValues="false"
              multiValued="true"
              sortMissingLast="false">
       <analyzer>
           <tokenizer class="solr.StandardTokenizerFactory"/>
           <filter class="solr.LowerCaseFilterFactory"/>
           <filter class="solr.ASCIIFoldingFilterFactory"/>
           <filter class="solr.EnglishPossessiveFilterFactory"/>
           <filter class="solr.StemmerOverrideFilterFactory" dictionary="overridenstemdict.txt"/>
           <filter class="solr.EnglishMinimalStemFilterFactory"/>
           <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/>
       </analyzer>
   </fieldType>
   <dynamicField name="*_cpt" type="joined_text" multiValued="true" indexed="true"/>
   <copyField source="*" dest="*_cpt"/>
   ```

4. The definition of the request handler should be added to _solrconfig.xml_:
   ```
   <!-- A request handler for the initialization query stage  -->
   <requestHandler name="/initialRH" class="com.lucidworks.fusion.semanticsearch.query.InitializationStageRequestHandler"/>
   ```
5. After that we must restart Solr.
