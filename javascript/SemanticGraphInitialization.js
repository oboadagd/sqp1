function semanticGraphInitialization(request, response ,ctx, solrServer, solrServerFactory) {
    // Configuration
    var stage = "SemanticGraphInitialization";
    var productCollection = "demo";
    var fieldType = "joined_text";
    var debug = true;

    // Initialize ctx
    ctx.debug = [];
    ctx.stages = [];

    // Invocation
    var params = new org.apache.solr.common.params.ModifiableSolrParams();
    params.add("query", request.getParams().get("q"))
    params.add("productCollection", productCollection)
    params.add("fieldType", fieldType)

    var req = new org.apache.solr.client.solrj.request.GenericSolrRequest(
        org.apache.solr.client.solrj.SolrRequest.METHOD.GET,
        "/initialRH",
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
