# SQP  boosting stage

The logic implementation for
[SQP Boosting Strategy Stage](../vector-search-integration/sqp-boosting-pipeline/SQPBoostingStrategy.js).

## Deployment
1. Add SQPBoostingStrategyStageRequestHandler description to Solr config file _solrconfig.xml_ in the product collection:
   ```
   <requestHandler name="/sqpBoostingRH" class="com.lucidworks.fusion.boosting.sqp.SQPBoostingStrategyStageRequestHandler"/>
   ```
