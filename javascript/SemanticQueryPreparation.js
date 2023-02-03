function semanticQueryPreparation(request, response, ctx, solrServer, solrServerFactory) {
    // Configuration
    var stage = "SemanticQueryPreparation";
    var debug = false;
    var numFoundThreshold = 100;
    var allowComprehensions = false;
    var stageName;
    var stageConfig = {};

    if (!response) {
        stageName = "exactMatch";
    } else {
        stageName = "relaxedMatch";
        stageConfig["linguisticsAllowed"] = true;
        stageConfig["spellcheckAllowed"] = true;
        stageConfig["considerTypePriority"] = false;
    }
    stageConfig["conceptFieldSuffix"] = "_cpt";
    if (!response || response.getInnerResponse().getNumFound().get() < numFoundThreshold) {

        // Invocation
        var params = new org.apache.solr.common.params.ModifiableSolrParams();
        var graph = ctx.get("graph");
        params.add("graph", graph);
        params.add("debug", debug);
        params.add("allowComprehensions", allowComprehensions);
        params.add("stageName", stageName);
        for (var param in stageConfig) {
            params.add(param, stageConfig[param]);
        }
        var req = new org.apache.solr.client.solrj.request.GenericSolrRequest(
            org.apache.solr.client.solrj.SolrRequest.METHOD.GET,
            "/query-preparation",
            params)
        var resp = solrServerFactory.request(req, solrServer);

        // Post processing
        var iterator = resp.iterator();
        while (iterator.hasNext()) {
            entry = iterator.next();
            if (entry.getKey().equals("responseHeader")) {
                continue;
            }
            request.putSingleParam(entry.getKey(), entry.getValue());
        }

    } else {
        request.putSingleParam("done", true);
    }
}
