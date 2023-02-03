function vectorSearchMock(request, response, ctx) {
    if (!response || response.getInnerResponse().getNumFound().get() >= 0) {
        var documents = response.getInnerResponse().getDocuments();
        var maxScore = response.getInnerResponse().getMaxScore().get();
        var results = [];
        var q = [];

        for (i = 0; i < documents.length && i < 100; i++) {
            var result =  documents[i].getField("id") + "^" + (documents[i].getField("score")/maxScore);
            var query =  "id:" + documents[i].getField("id") + "^=" + (documents[i].getField("score")/maxScore);
            results.push(result);
            q.push(query);
        }

        var vectorSearchResults = "(" + java.lang.String.join(" OR ", results) + ")";
        var vectorSearchQuery = java.lang.String.join(" OR ", q);
        ctx.put("vectorSearchResults", vectorSearchResults);
        request.putSingleParam("q", vectorSearchQuery);
    }
}