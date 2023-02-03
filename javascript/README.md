# Fusion Query Pipeline stages

## Structure
All Query pipeline stages based on pure JavaScript stages for PoC phase.
In each stage there are some main parts:
* Configuration - reflects configurable parameters that should go into a stage 
properties once it'll be implemented as native stage
* Invocation - call to Solr plug-in with algorithm implementation 
* Post processing - saving of the stage results to be used by downstream stages

## Stage details

### [Semantic Graph Initialization](SemanticGraphInitialization.js)
Initialize a graph object for the next semantic stages. An incoming query
processed by an analyzer before graph building, i.e graph will
be based on normalized terms. 
Also, this stage creates context variables to be used for storing and processing 
debug information:
```
// Initialize ctx
ctx.debug = [];
ctx.stages = [];
```

**Logic implementation**: [initialization stage](../initialization-stage).

**Usage scenarios**:
* It's a mandatory step for semantic analysis. Should be enabled and placed in 
front of other semantic steps.

**Configuration**:
See README.md in [corresponding module](../initialization-stage/README.md).



### [SemanticGraphConceptualization](SemanticGraphConceptualization.js)
Performs the graph enrichment based on concept information stored in 
configured concept collection. As a result, graph may get additional edges (for
multiple-word concepts) and updated original edges. If any concepts or shingles 
found, this information will be added to edges as additional attributes.

**Logic implementation**: [conceptualization stage](../conceptualization-stage).

**Usage scenarios**:
* Enable it if you want to enrich a graph with the information about attributes. 
* Enable it if you are going to build high-precision concept queries to Solr.

**Configuration**:
See README.md in [corresponding module](../conceptualization-stage/README.md).



### [Semantic Graph Spellcheck](SemanticGraphSpellcheck.js)
Intended for spell checking requested phrase and fix misspellings.

**Logic implementation**: [spellcheck stage](../spellcheck-stage).

**Usage scenarios**:
* Enable it if you want to enrich a graph with spell corrected edges.

**Configuration**:
See README.md in [corresponding module](../spellcheck-stage/README.md).



### [Semantic Graph Comprehension](SemanticGraphComprehension.js)
Using this stage to enrich a graph with special filter or sorting edges based on predefined patterns. 
E.g. “under 20$” will be added as a filter by price or “popular” will be as sorting by feedback.

**Logic implementation**: [comprehension stage](../comprehension-stage).

**Usage scenarios**:
* Enable it if you want to use comprehension patterns in the search.

**Configuration**:
See README.md in [corresponding module](../comprehension-stage/README.md).



### [Semantic Graph Weighting](SemanticGraphWeighting.js)
Using this stage for adding weights to concept fields.
They are used by query preparation stages and boosting strategies based on SQP.

**Logic implementation**: [weighting stage](../weighting-stage).

**Usage scenarios**:
* Enable it if you want to apply weights for concepts.

**Configuration**:
See README.md in [root package](../README.md).



### [Semantic Graph Linguistic](SemanticGraphLinguistic.js)
The linguistic expansion could include more complex process than usual synonym enrichment, 
so we kept it as a separate step for user. In order to perform this processing in query 
pipeline (that is much convenient for users than indexed synonyms), we’re going to use 
a separate collection where each linguistic pair will be store with different types: synonym
or hyponym/hypernym.

**Logic implementation**: [linguistic stage](../linguistic-expansion-stage).

**Usage scenarios**:
* Enable it if you want to enrich a graph with synonyms and hyponyms/hypernyms.

**Configuration**:
See README.md in [corresponding module](../linguistic-expansion-stage/README.md).



### [Semantic Query Preparation](SemanticQueryPreparation.js)
Semantic query preparation stage generates a Solr query based on the semantic graph.
Initialization, conceptualization and weighting stages must placed before,
other query stages are optional. A generated query will replace original `q`-parameter.
Also, it may add some extra parameters to the Fusion's request object.
This stage can persist in the pipeline several times with different parameters,
but in this case query pipeline should look like:
* SemanticQueryPreparation1
* SolrQuery
* SemanticQueryPreparation2
* SolrQuery
* SemanticQueryPreparation3
* SolrQuery
So on each preparation stage pipeline's request object will be overwritten and then sent to Solr.

**Logic implementation**: [query preparation stage](../query-preparation-stage).

**Usage scenarios**:
* Enable it if you want to generate a Solr query based on the semantic graph.

**Configuration**:
See README.md in [corresponding module](../query-preparation-stage/README.md).



### [Semantic Graph Debug](SemanticGraphDebug.js)
Takes data from "ctx.debug" and "ctx.stage" and saves them into the response.
Except their common logic, all other stages should push the debug data and stage
name into the arrays "ctx.debug" and "ctx.stages" accordingly base on stage 
configuration. Notice, these data should be pushed or not pushed simultaneously 
otherwise "Semantic Graph Debug" stage will work incorrectly.
If you want to use a different external visualizer, please save its URL into 
the variable "externalVisualizerURL".

**Logic implementation**: no separated logic.

**Usage scenarios**:
* Enable the stage if you want to debug semantic analysis. Once the stage enabled,
you can see graph representations in `fusion` object inside JSON representation of the response.
* Disable ths stage for production usage.
 
**Configuration**:
* externalVisualizerURL (String) - name of the 3rd-party .dot visualizer. It 
should support graph visualization from path variable.


## Deployment
To deploy any stage, perform following actions:
0. Execute deployment instructions form related logic implementation project.
1. Create new JavaScript stage
2. Fill `Label` field in accordance with the name of stage (it's better to use 
.js file name)
3. Copy `Script Body` from relate .js file
4. `Apply` stage configuration and put it into appropriate place in the pipeline
