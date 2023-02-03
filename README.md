# Semantic Search 
This is the repo for the semantic query parsing prototype project. The prototype will include: 
- Concept auto-tagging;
- Concept indexing;
- Connectors integration;
- Semantic query parsing;
- Query expansion integration;
- Query parsing explanation;
- Graph visualization;
- Concept-oriented Solr query generation;

## Project purpose 
The Semantic Search is a powerful approach which allows significantly increase precission
of query analysis on deep-parametrised e-commerce catalogs.
Semantic search integration into Lucidworks Fusion has a big potential and allows Fusion users
utilize their catalog information much effectively. This capability should be available for 
them as pipeline stages which can be easily enabled and configured.

## Project structure
The high-level structure is described in the [design document](https://docs.google.com/document/d/1e15YB0qto91gg8cR5hqAxhPfNQsdD-Tj3yG6JpWqYPQ)

Folders in this repo contains stage implementations or utilities required for 
development. Please, refer to readme files inside them.

Please, refer to [Stage README](./javascript/README.md) to get detailed 
description of stages and their usage scenarios.  

## Deployment
In order to deploy solution, you need:
* Installed and launched Docker for Desktop
* Installed kubectl
* Kubectl config file for the target cluster should be placed in ```~/.kube/```. 

> You can modify and run the [deployment script](./deploySolrPlugins.sh) instead 
> manual execution of non-optional steps 

1. Build the whole project by Gradle
    ```
    ./gradlew build prepareSolrExtensions
    ```
2. Define parameters you're going to use for image uploading
    ```
    DOCKERHUB_LOGIN=<Your dockerhub login>
    DOCKERHUB_REPO=<Your dockerhub repo>
    DOCKERHUB_TAG=<incremental version number>
    ```
3. Prepare docker image to upload to Fusion server
    ```
    docker build -t ${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} ./
    ```
4. Upload built image to dockerhub
    ```
    docker login
    docker push ${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG}
    ```
5. Deploy image to the cluster
    ```
    kubectl config use-context gke_lw-sales_us-west1-a_partner-cluster
    kubectl set image deployment/grid-dynamics-solr-exporter solr-init=${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} --record
    kubectl set image deployment/grid-dynamics-solr-exporter exporter=${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} --record
    kubectl set image statefulset/grid-dynamics-solr solr=${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} --record
    ```
6. (Optional) Check if a cluster contains your library now. It can take up to 5 
minutes to re-launch solr instances in the cluster. 
    ```
    kubectl exec grid-dynamics-solr-0 -c solr -- ls /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
    ```

## Debugging

In order to debug Solr plugins on a local machine, perform the next stages:
1. Download [Solr](https://lucene.apache.org/solr/downloads.html) and setup it 
to local machine
2. Navigate to the Solr home folder, e.g.:
    ```
    cd /opt/solr-8.7.0 
    ```
3. Run solr in cloud initialization mode:
    ```
    bin/solr -e cloud -noprompt
    ```
4. Deploy solution to local Solr by 
[local deployment script](./deploySolrPlugins-local.sh)
5. Execute deployment instructions from README
files of the stage projects against local solr:
   * [initialization-stage](./initialization-stage/README.md)
   * [conceptualization-stage](./conceptualization-stage/README.md)
6. Connect to the local Solr installation by Remote Java debugger on 
`localhost:18983`  

## Installation

### Collection

1. All collections should follow the requirements as below:
    * Placed in the same Solr cluster
    * Consists of ONE shard (except for Product collection, it can be sharded)

2. The following collections should be created:

     |Collection    |Contains                              |Name convention                   |Initialization                                            |
     |--------------|--------------------------------------|----------------------------------|----------------------------------------------------------|
     |Product       |Main catalog - products               |`<application_name>`              |[Manual](./best-buy-catalog/best_buy_catalog_partial.json)|
     |Concept       |Concepts and shingles                 |`<application_name>_concept`      |[Automatic](./README.md#Index Pipeline)                   |
     |Synonym       |Linguistics - synonyms, hyponyms, etc.|`<application_name>_synonym`      |[Manual](./best-buy-catalog/synonyms.csv)                 |
     |Comprehension |Comprehension patterns with actions   |`<application_name>_comprehension`|[Manual](./best-buy-catalog/comprehensions.json)          |
     |Weights       |Weights for fields                    |`<application_name>_weights`      |[Manual](./best-buy-catalog/weights.json)                 |

3. The following [Solr configuration files](./solr-config) should be overwritten for each collection:
    * `managed-schema` - first
    * `solrconfig.xml` - second

### Index Pipeline

Index pipeline should consist of the following stages:
1. `Field Mapping` - optional
2. `Concept Indexing Stage` - required
3. `Solr Indexer` - required

#### [Concept Indexing Stage](./concept-indexing-stage)

1. Assemble plugin running corresponding Gradle:
    ```
    ./gradlew concept-indexing-stage:assemblePlugin
   ``` 
2. Assembled indexing stage will be placed here:
    ```
    ./concept-indexing-stage/build/libs/concept-indexing-stage-0.0.1.zip
    ```
3. Load zip-file to the Fusion:
    1. `SYSTEM` -> `Blobs`
    2. `Add +` -> `Index Stage Plugin`
    3. `Upload Blob` -> `Choose file`
    4. Select assembled zip-file
    5. `Upload`

4. Indexing stage is ready to be included in any index pipeline. It will be available in the `Custom`-section in the stages list.
    
### Query Pipeline

The order of query stages in the query pipeline should be the same as they mentioned below.

#### [Initialization Stage](./initialization-stage)

1. Add custom JavaScript Stage to the query pipeline
2. Set `Label`, e.g. as `SemanticGraphInitialization`
3. Set `Script Body` from the [SemanticGraphInitialization.js](./javascript/SemanticGraphInitialization.js)

#### [Comprehension Stage](./comprehension-stage)

1. Add custom JavaScript Stage to the query pipeline
2. Set `Label`, e.g. as `SemanticGraphComprehension`
3. Set `Script Body` from the [SemanticGraphComprehension.js](./javascript/SemanticGraphComprehension.js)

#### [Conceptualization Stage](./conceptualization-stage)

1. Add custom JavaScript Stage to the query pipeline
2. Set `Label`, e.g. as `SemanticGraphConceptualization`
3. Set `Script Body` from the [SemanticGraphConceptualization.js](./javascript/SemanticGraphConceptualization.js)

#### [Linguistic Stage](./linguistic-expansion-stage)

1. Add custom JavaScript Stage to the query pipeline
2. Set `Label`, e.g. as `SemanticGraphLinguistic`
3. Set `Script Body` from the [SemanticGraphLinguistic.js](./javascript/SemanticGraphLinguistic.js)

#### [Weighting Stage](./weighting-stage)

1. Add custom JavaScript Stage to the query pipeline
2. Set `Label`, e.g. as `SemanticGraphWeighting`
3. Set `Script Body` from the [SemanticGraphWeighting.js](./javascript/SemanticGraphWeighting.js)

#### [Query Preparation Stage](./query-preparation-stage)

1. Add custom JavaScript Stage to the query pipeline
2. Set `Label`, e.g. as `SemanticQueryPreparation`
3. Set `Script Body` from the [SemanticQueryPreparation.js](./javascript/SemanticQueryPreparation.js)

#### Solr Query

* Add default `SolrQuery` stage from the `Fetch Data` section to the query pipeline

#### Debug Stage

1. Add custom `JavaScript` stage to the query pipeline
2. Set `Label`, e.g. as `SemanticGraphDebug`
3. Set `Script Body` from the [SemanticGraphDebug.js](./javascript/SemanticGraphDebug.js)

## Configuration

### [Data](./best-buy-catalog)

### Linguistics

Linguistics, i.e. synonyms and hyperonyms/hyponyms, represent a [dictionary](./best-buy-catalog/synonyms.csv) with the following format:
1. `source_word` - a word which can be replaced
2. `synonym_word` - a work which is replacement for the corresponding `source_word`
3. `linguistic_type` - relation between `synonym_word` and `source_word`:
    * `SYN` - for synonyms
    * `HYP` - for hyperonyms and hyponyms
    
### Comprehensions

Comprehension types:
1. `Sort` - comprehensions which apply sorting conditions based on some pattern
2. `Filter` - comprehensions which apply filters based on some pattern

Comprehensions represent a [dictionary](./best-buy-catalog/comprehensions.json) with the following format:
1. `key` - an anchor which allows finding special conditions which could be applied on a part of the search phrase
2. `type` - comprehension type:
    * `sort` - comprehensions which apply sorting conditions on the main query result, e.g. by price, date, etc.
    * `filter` - comprehensions which apply special filters to the main query, e.g. by size, price range, etc.
3. `field` - product field name by which it should be filtered or sorted
4. `order` - for `sort` comprehensions only - sorting order - `ASC` or `DESC`
5. `patterns` - for `filter` comprehensions only - ordered list of patterns, i.e. regular expressions, which should all match terms in the search graph relatively to the `key`, i.e. anchor, included in the list as is
6. `operator` - for `filter` comprehensions only - filter operator - `LESS` <=> `[* TO value]`, `MORE` <=> `[value TO *]`  or `BETWEEN` <=> `[value1 TO value2]`

### Index Pipeline

#### [Concept Indexing Stage](./concept-indexing-stage)

Configuration parameters:

|Parameter               |Type   |Value                                  |Description                                                  |
|------------------------|-------|---------------------------------------|-------------------------------------------------------------|
|`Analysis field type`   |String |`joined_text`                          |Field type to be used to concepts analysis                   |
|`ignoreFields`          |Strings|[`name`, `regularPrice`, `releaseDate`]|Product fields to be ignored for concept indexing            |
|`Minimum shingle tokens`|Integer|`5` - default                          |Minimum quantity of tokens to slice the input to shingles    |
|`Maximum concept tokens`|Integer|`4` - default                          |Maximum quantity of tokens to consider the input as a concept|
|`Shingle length`        |String |`2-3` - recommended                    |Shingles consist of 2 and 3 concept's terms                  |
|                        |       |`3-4`                                  |Shingles consist of 3 and 4 concept's terms                  |
|                        |       |`2-4`                                  |Shingles consist of 2, 3 and 4 concept's terms               |

### Query Pipeline

#### [Initialization Stage](./javascript/SemanticGraphInitialization.js)

Configuration parameters:

|Parameter          |Type   |Value                        |Description                                             |
|-------------------|-------|-----------------------------|--------------------------------------------------------|
|`productCollection`|String |`<application_name>`         |Product collection name                                 |
|`fieldType`        |String |`joined_text`                |Field type to be used for initial search phrase analysis|
|`debug`            |Boolean|`true`                       |Report with debug info enabled                          |
|                   |       |`false`                      |Report with debug info disabled (production recommended)|
|`stage`            |String |`SemanticGraphInitialization`|Name of this stage used to be shown in debug reports    |

#### [Comprehension Stage](./javascript/SemanticGraphComprehension.js)

Configuration parameters:

|Parameter   |Type   |Value                             |Description                                                              |
|------------|-------|----------------------------------|-------------------------------------------------------------------------|
|`dictionary`|String |`<application_name>_comprehension`|Collection name with comprehension patterns                              |
|`debug`     |Boolean|`true`                            |Report with debug info enabled                                           |
|            |       |`false`                           |Report with debug info disabled (production recommended)                 |
|`stage`     |String |`SemanticGraphComprehension`      |Name of this stage used to be shown in debug reports                     |

#### [Conceptualization Stage](./javascript/SemanticGraphConceptualization.js)

Configuration parameters:

|Parameter            |Type   |Value                           |Description                                             |
|---------------------|-------|--------------------------------|--------------------------------------------------------|
|`collection`         |String |`<application_name>_concept`    |Concept collection name                                 |
|`debug`              |Boolean|`true`                          |Report with debug info enabled                          |
|                     |       |`false`                         |Report with debug info disabled (production recommended)|
|`stage`              |String |`SemanticGraphConceptualization`|Name of this stage used to be shown in debug reports    |

#### [Spellcheck Stage](./javascript/SemanticGraphSpellcheck.js)

Configuration parameters:

|Parameter               |Type   |Value                       |Description                                             |
|------------------------|-------|----------------------------|--------------------------------------------------------|
|`spellCheckerCollection`|String |`<application_name>`        |Collection name to perform spell checker lookup         |
|`accuracy`              |Float  |`0.67`                      |Spell checker accuracy                                  |
|`debug`                 |Boolean|`true`                      |Report with debug info enabled                          |
|                        |       |`false`                     |Report with debug info disabled (production recommended)|
|`stage`                 |String |`SemanticGraphSpellcheck`   |Name of this stage used to be shown in debug reports    |

#### [Linguistic Stage](./javascript/SemanticGraphLinguistic.js)

Configuration parameters:

|Parameter             |Type   |Value                       |Description                                             |
|----------------------|-------|----------------------------|--------------------------------------------------------|
|`linguisticCollection`|String |`<application_name>_synonym`|Concept collection name                                 |
|`windowSize`          |Integer|`3`                         |Number of tokens involved to complex synonyms analysis  |
|`debug`               |Boolean|`true`                      |Report with debug info enabled                          |
|                      |       |`false`                     |Report with debug info disabled (production recommended)|
|`stage`               |String |`SemanticGraphLinguistic`   |Name of this stage used to be shown in debug reports    |

#### [Weighting Stage](./javascript/SemanticGraphWeighting.js)

Configuration parameters:

|Parameter                  |Type   |Value                       |Description                                                         |
|---------------------------|-------|----------------------------|--------------------------------------------------------------------|
|`weightingCollection`      |String |`<application_name>_weights`|Collection name with weights for the fields                         |
|`conceptBoost`             |Integer|`50`                        |Default weight (boost) for concept edges in the semantic graph      |
|`shingleBoost`             |Integer|`20`                        |Default weight (boost) for shingle edges in the semantic graph      |
|`textBoost`                |Integer|`5`                         |Default weight (boost) for text edges in the semantic graph         |
|`wordAmountBoostMultiplier`|Float  |`1.1`                       |Edges containing more words have bigger boost                       |
|`stage`                    |String |`SemanticGraphWeighting`    |Name of this stage used to be shown in debug reports                |

#### [Query Preparation Stage](./javascript/SemanticQueryPreparation.js)

Configuration parameters:

|Parameter                  |Type   |Value                     |Description                                                          |
|---------------------------|-------|--------------------------|---------------------------------------------------------------------|
|`numFoundTreshold`         |Integer|`100`                     |Number of found products from a previous stage needed to skip current|
|`allowComprehensions`      |Boolean|`true`                    |Using of comprehensions in query generation                          |
|                           |       |`false`                   |Skip comprehensions                                                  |
|`stage`                    |String |`SemanticQueryPreparation`|Name of this stage used to be shown in debug reports                 |
|`debug`                    |Boolean|`true`                    |Report with debug info enabled                                       |
|                           |       |`false`                   |Report with debug info disabled (production recommended)             |

#### [Semantic Query Preparation Boosting Stage]

Configuration parameters:

|Parameter                  |Type   |Value                     |Description                                                          |
|---------------------------|-------|--------------------------|---------------------------------------------------------------------|
|`allowPartialMatch`        |Boolean|true                     |Allowing partial matching                                             |
|                           |       |false                    |Disallowing partial matching                                          | 
|`debug`                    |Boolean|true                     |Report with debug info enabled                                        |
|                           |       |false                    |Report without debug info enabled                                     |


Exact match stage configuration parameters:

|Parameter           |Type   |Value |Description                                                   |
|--------------------|-------|------|--------------------------------------------------------------|
|`conceptFieldSuffix`|String |`_cpt`|Suffix to be appended to the field name found in concept index|

Relaxed match stage configuration parameters:

|Parameter             |Type   |Value  |Description                                                                 |
|----------------------|-------|-------|----------------------------------------------------------------------------|
|`conceptFieldSuffix`  |String |`_cpt` |Suffix to be appended to the field name found in concept index              |
|`linguisticsAllowed`  |Boolean|`true` |Using of synonyms and hyponyms/hyperonyms for query generation **(default)**|
|                      |Boolean|`false`|Rejecting of synonyms and hyponyms/hyperonyms                               |
|`spellcheckAllowed`   |Boolean|`true` |Using of spell corrected terms **(default)**                                |
|                      |Boolean|`false`|Rejecting of spell corrected terms                                          |
|`considerTypePriority`|Boolean|`true` |Using of concept type with the highest priority for a particular edge       |
|                      |Boolean|`false`|Using of all fields for query generation **(default)**                      |

#### [Debug Stage](./javascript/SemanticGraphDebug.js)

Configuration parameters:

|Parameter              |Type  |Value                                        |Description                            |
|-----------------------|------|---------------------------------------------|---------------------------------------|
|`externalVisualizerURL`|String|`https://dreampuf.github.io/GraphvizOnline/#`|URL for the 3rd-party `.dot` visualizer|
