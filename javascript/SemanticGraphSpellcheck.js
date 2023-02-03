function semanticGraphConceptualization(request, response ,ctx, solrServer, solrServerFactory) {
    // Configuration
    var stage = "SemanticGraphSpellcheck";
    var spellCheckerCollection = "demo";
    var accuracy = 0; //value for parameter spellcheck.accuracy
    var debug = true;

    // Invocation
    var params = new org.apache.solr.common.params.ModifiableSolrParams();
    var graph = ctx.get("graph");
    params.add("graph", graph);
    params.add("spellCheckerCollection", spellCheckerCollection);
    params.add("accuracy", accuracy);
    var req = new org.apache.solr.client.solrj.request.GenericSolrRequest(
        org.apache.solr.client.solrj.SolrRequest.METHOD.GET,
        "/spellcheckRH",
        params);
    var resp = solrServerFactory.request(req, solrServer);

    // Post processing
    graph = resp.get("graph");
    ctx.put("graph", graph);

    if (debug){
        ctx.debug.push(graph);
        ctx.stages.push("stage_" + stage);
    }
}