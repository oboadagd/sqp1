# MainQueryWorkbench stages

## Structure

MainQueryWorkbench consists of the following stages:
* MainSearch - mimics a main customer's search logic.
* ZeroResultCheck - checks if the main search stage returned zero results.
* NeverNull - calls Never Null query pipeline in case of ZRQ.

## Configuration

### MainSearch

* Fusion query stage type - `Call Pipeline`
* Label - `MainSearch`
* Query Pipeline to Call - [MainSearchPipeline](../main-search-pipeline/README.md)

### ZeroResultCheck

* Fusion query stage type - `JavaScript`
* Label - `ZeroResultCheck`
* Script Body - [ZeroResultCheck](ZeroResultCheck.js)

### NeverNull

* Fusion query stage type - `Call Pipeline`
* Label - `NeverNull`
* Query Pipeline to Call - [NeverNullQueryPipeline](../never-null-query-pipeline/README.md)
* Condition:
```
!request.hasParam("isZRQ") || request.getFirstParam("isZRQ").equals("true")
```
