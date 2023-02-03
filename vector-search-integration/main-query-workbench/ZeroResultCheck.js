function zeroResultCheck(request, response, ctx) {
    request.putSingleParam("isZRQ", !response || response.getInnerResponse().getNumFound().get() == 0);
}