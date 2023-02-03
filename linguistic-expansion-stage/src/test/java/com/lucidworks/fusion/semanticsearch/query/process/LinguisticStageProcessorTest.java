package com.lucidworks.fusion.semanticsearch.query.process;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.client.impl.InternalSolrJClient;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinguisticStageProcessorTest {
    private InternalSolrJClient client;
    private LinguisticStageConfig config;
    private LinguisticStageProcessor processor;

    @Before
    public void init() {
        client = Mockito.mock(InternalSolrJClient.class);
        config = new LinguisticStageConfig("synonym_core", "/linguisticRH", 3);
        processor = new LinguisticStageProcessor();
    }

    @Test
    public void checkProcessShouldReturnListOfEdges() throws Exception {
        var graph = importFromDotFile("graphTest1.dot");
        Mockito.when(client.get(Mockito.anyString(), Mockito.anyMap())).thenReturn(getSolrDocumentListForComplexWordsTest());
        SemanticGraph result = processor.process(client, config, graph);
        Set<SemanticEdge> edges = result.edgeSet();

        assertTrue(graph.edgeSet().stream().anyMatch(edges::contains));
        assertEquals(7, result.edgeSet().size());
        assertTrue(edges.stream().anyMatch(e -> e.getToken().equals("metal")&&e.getSource().equals("1")&&e.getTarget().equals("4")));
        assertTrue(result.edgesOf("1").stream().anyMatch(e -> e.getToken().equalsIgnoreCase("metal")));
    }

    @Test
    public void shouldEnrichGraphBySynonymsTest() throws IOException {
        SemanticGraph graphSource = importFromDotFile("graphTest2.dot");
        SemanticGraph graph = importFromDotFile("graphTest2.dot");
        processor.enrichGraphBySynonyms(getSolrDocumentListForEnrichmentTest(), graph, config);

        Set<SemanticEdge> source = graphSource.edgeSet();
        Set<SemanticEdge> target = graph.edgeSet();
        List<SemanticEdge> compareResult = source.stream().filter(two -> target.stream()
                .anyMatch(one -> one.isEqualTo(two)))
                .collect(Collectors.toList());

        assertEquals(4, compareResult.size());
        assertTrue(compareResult.stream().anyMatch(e->e.getToken().equals("heavy")));
        assertTrue(compareResult.stream().anyMatch(e->e.getToken().equals("metal")));
        assertTrue(compareResult.stream().anyMatch(e->e.getToken().equals("iron")));
        assertTrue(compareResult.stream().anyMatch(e->e.getToken().equals("rock")));
        assertTrue(graph.edgeSet().stream().anyMatch(e -> e.getToken().equals("metal")&&e.getSource().equals("3")&&e.getTarget().equals("4")));
    }

    @Test
    public void findSynonymsInSolrDocumentListTest() {
        String testTag = "metal";
        var solrDocumentList = getSolrDocumentListForEnrichmentTest();
        List<SemanticEdge> edgeList = processor.findSynonymsInSolrDocumentList(solrDocumentList, testTag);
        assertTrue(edgeList.stream().anyMatch(e -> e.getToken().equalsIgnoreCase((String) ((List)solrDocumentList.get(0).get("synonym_word")).get(0))));
        assertTrue(testTag.equalsIgnoreCase((String) ((List)solrDocumentList.get(0).get("source_word")).get(0)));
    }

    private List<LinkedHashMap> getSolrDocumentListForEnrichmentTest() {
        List<LinkedHashMap> solrDocumentList = new ArrayList<>();

        LinkedHashMap<String, List<String>> map1 = new LinkedHashMap<>();
        LinkedHashMap<String, List<String>> map2 = new LinkedHashMap<>();

        map1.put("id", singletonList("10125"));
        map1.put("source_word", singletonList("metal"));
        map1.put("synonym_word", singletonList("heavy metal rock"));
        map1.put("linguistic_type", singletonList("SYN"));

        map2.put("id", singletonList("10126"));
        map2.put("source_word", singletonList("rock"));
        map2.put("synonym_word", singletonList("metal"));
        map2.put("linguistic_type", singletonList("HYP"));

        solrDocumentList.add(map1);
        solrDocumentList.add(map2);

        return solrDocumentList;
    }

    private LinkedHashMap getSolrDocumentListForComplexWordsTest() {
        List<LinkedHashMap> solrDocumentList = new ArrayList<>();

        LinkedHashMap<String, List<String>> map1 = new LinkedHashMap<>();
        LinkedHashMap<String, List<String>> map2 = new LinkedHashMap<>();

        map1.put("id", singletonList("10125"));
        map1.put("source_word", singletonList("heavy metal rock"));
        map1.put("synonym_word", singletonList("metal"));
        map1.put("linguistic_type", singletonList("SYN"));

        map2.put("id", singletonList("10126"));
        map2.put("source_word", singletonList("rock"));
        map2.put("synonym_word", singletonList("rock-n-roll"));
        map2.put("linguistic_type", singletonList("SYN"));

        solrDocumentList.add(map1);
        solrDocumentList.add(map2);

        LinkedHashMap response = new LinkedHashMap<>();
        response.put("response", solrDocumentList);
        return response;
    }

    private SemanticGraph importFromDotFile(String dotFileName) throws IOException {
        String file = Objects.requireNonNull(getClass().getClassLoader().getResource(dotFileName)).getFile();
        String graphInDot = (String.join(" ", Files.readAllLines(Paths.get(file))));
        return SemanticGraph.fromDot(graphInDot);
    }
}
