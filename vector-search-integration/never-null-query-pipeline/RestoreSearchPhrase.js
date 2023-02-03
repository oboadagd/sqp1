function restoreSearchPhrase(request, response, ctx) {
    var searchPhrase = ctx.get("searchPhrase");
    request.putSingleParam("q", searchPhrase);
}