package com.lucidworks.fusion.semanticsearch.query.process;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticProcessor;
import com.lucidworks.fusion.semanticsearch.common.client.RestClient;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class LinguisticStageProcessor implements SemanticProcessor<LinguisticStageConfig> {

    @Override
    public SemanticGraph process(RestClient client, LinguisticStageConfig config, SemanticGraph graph) throws Exception {
        String query = createPhraseForRequest(graph, config);

        List<LinkedHashMap> solrDocumentList = (List) callSynonymCollection(client, config, query).get("response");

        SemanticGraph enrichGraphBySynonyms = enrichGraphBySynonyms(solrDocumentList, graph, config);
        log.debug("enrichGraphBySynonyms: " + enrichGraphBySynonyms);

        return enrichGraphBySynonyms;
    }

    //TODO: cover with unit tests
    public String createPhraseForRequest(SemanticGraph graph, LinguisticStageConfig config) {
        List<GraphPath<String, SemanticEdge>> paths = graph.getAllSubPaths(config.getWindowSize());

        StringBuilder sb = new StringBuilder();
        for (GraphPath<String, SemanticEdge> path : paths) {
            String queryFromPath = path.getEdgeList().stream()
                    .map(SemanticEdge::getToken)
                    .reduce("source_word:\"", (acc, curr) -> acc + curr + " ")
                    .trim();
            sb.append(queryFromPath);
            sb.append("\"" + " ");
        }

        return sb.toString().trim();
    }

    public SemanticGraph enrichGraphBySynonyms(List<LinkedHashMap> solrDocumentList,
                                               SemanticGraph graph,
                                               LinguisticStageConfig config) {
        for (GraphPath<String, SemanticEdge> path : graph.getAllSubPaths(config.getWindowSize())) {
            String queryFromPath = path.getEdgeList().stream()
                    .map(SemanticEdge::getToken)
                    .collect(Collectors.joining(" "));
            String startVertex = path.getStartVertex();
            String endVertex = path.getEndVertex();

            List<SemanticEdge> edges = findSynonymsInSolrDocumentList(solrDocumentList, queryFromPath);
            edges.forEach(e -> graph.addEdge(startVertex, endVertex, e));
        }
        return graph;
    }

    public List<SemanticEdge> findSynonymsInSolrDocumentList(List<LinkedHashMap> solrDocumentList, String tag) {
        List<SemanticEdge> synonymsList = new ArrayList<>();
        for (int i = 0; i < solrDocumentList.size(); i++) {
            String source = (String) ((List) (solrDocumentList.get(i).get("source_word"))).get(0);
            String syn = (String) ((List) ((solrDocumentList.get(i).get("synonym_word")))).get(0);
            String type = (String) ((List) ((solrDocumentList.get(i).get("linguistic_type")))).get(0);

            if (tag.equalsIgnoreCase(source)) {
                SemanticEdge newEdge = new SemanticEdge();
                newEdge.setToken(syn);
                newEdge.getModifiers().add(SemanticModifier.valueOf(type));
                synonymsList.add(newEdge);
            }
        }
        return synonymsList;
    }


    private Map<String, Object> callSynonymCollection(RestClient client, LinguisticStageConfig config, String query) throws Exception {
        var params = new HashMap<String, String>();
        params.put("q", query);
        params.put("wt", "json");
        params.put("rows", "500");
        return client.get(config.getSynonymCollection() + config.getSynonymEndpoint(), params);
    }

}
