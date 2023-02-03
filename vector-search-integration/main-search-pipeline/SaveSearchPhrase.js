function saveSearchPhrase(request, response, ctx) {
    var searchPhrase = new java.lang.String(request.getParams().get("q")[0]);
    ctx.put("searchPhrase", searchPhrase);
}