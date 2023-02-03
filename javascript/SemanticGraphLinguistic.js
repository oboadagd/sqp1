function semanticGraphLinguistic(request, response ,ctx, solrServer, solrServerFactory) {
    // Configuration
    var stage = "SemanticGraphLinguistic";
    var linguisticCollection = "demo_synonym";
    var debug = true;
    var windowSize = "3";

    // Invocation
    var params = new org.apache.solr.common.params.ModifiableSolrParams();
    params.add("graph", ctx.get("graph"))
    params.add("lingcoll", linguisticCollection)
    params.add("windowSize", windowSize)

    var req = new org.apache.solr.client.solrj.request.GenericSolrRequest(
        org.apache.solr.client.solrj.SolrRequest.METHOD.GET,
        "/linguisticRH",
        params)
    var resp = solrServerFactory.request(req, solrServer);

    // Post processing
    var graph = resp.get("graph");
    ctx.put("graph", graph);

    if (debug){
        ctx.debug.push(graph);
        ctx.stages.push("stage_" + stage);
    }
}
