function semanticGraphWeighting(request, response, ctx, solrServer, solrServerFactory) {
    // Configuration
    var stage = "Weighting";
    var weightingCollection = "demo_weights";
    var debug = true;
    var conceptBoost = 50;
    var shingleBoost = 20;
    var textBoost = 5;
    var wordAmountBoostMultiplier = 1.1;

    // Invocation
    var params = new org.apache.solr.common.params.ModifiableSolrParams();
    var graph = ctx.get("graph");
    params.add("graph", graph)
    params.add("weightingCollection", weightingCollection)
    params.add("endpoint", "/select")
    params.add("conceptBoost", conceptBoost)
    params.add("shingleBoost", shingleBoost)
    params.add("textBoost", textBoost)
    params.add("wordAmountBoostMultiplier", wordAmountBoostMultiplier)

    var req = new org.apache.solr.client.solrj.request.GenericSolrRequest(
        org.apache.solr.client.solrj.SolrRequest.METHOD.GET,
        "/weighting",
        params)
    var resp = solrServerFactory.request(req, solrServer);
    graph = resp.get("graph")
    ctx.put("graph", graph);

    if (debug){
        ctx.debug.push(graph);
        ctx.stages.push("stage_" + stage);
    }
}