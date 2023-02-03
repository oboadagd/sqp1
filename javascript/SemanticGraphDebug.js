function semanticGraphDebug (request, response, ctx) {
    // Configuration
    var externalVisualizerURL = "https://dreampuf.github.io/GraphvizOnline/#";
    var isHorizontal = true;

    // Post processing
    if (ctx.debug != null && ctx.stages != null) {
        for (index in ctx.debug) {
            var debugData = new javax.ws.rs.core.MultivaluedHashMap();
            var link = [];
            var dot = [];
            var graph = ctx.debug[index];

            if (isHorizontal) {
                graph = graph.replace('{', '{rankdir="LR";');
            }

            dot.push(graph);
            link.push(externalVisualizerURL + encodeURIComponent(graph));

            debugData.put("semantic-graph-dot", dot);
            debugData.put("semantic-graph-link", link);
            response.getInnerResponse().appendMultiMap(ctx.stages[index], debugData);
        }
    }
}