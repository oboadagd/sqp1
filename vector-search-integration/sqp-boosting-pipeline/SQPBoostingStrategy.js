function sqpBoostingStrategy(request, response ,ctx, solrServer, solrServerFactory) {
    // Configuration
    var stage = "SQPBoostingStrategy";
    var debug = false;
    var allowPartialMatch = true;


    // Invocation
    var params = new org.apache.solr.common.params.ModifiableSolrParams();
    params.add("debug", debug);
    params.add("vectorSearchQuery", ctx.get("vectorSearchResults"));
    params.add("graph", ctx.get("graph"));
    params.add("allowPartialMatch", allowPartialMatch);

    var req = new org.apache.solr.client.solrj.request.GenericSolrRequest(
        org.apache.solr.client.solrj.SolrRequest.METHOD.GET,
        "/sqpBoostingRH",
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
}