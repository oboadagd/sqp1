package com.lucidworks.fusion.boosting.sqp;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticTransformer;
import com.lucidworks.fusion.semanticsearch.common.model.Field;
import com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQPBoostingStrategyTransformerTest {

    private static final int VS_RESULT_LEN = 100;
    private static final String VS_QUERY_PATTERN = "\\+\\(((\\(|\\s)\\(\\(id:\"\\d+\"\\)\\^=1\\.0\\)){" + VS_RESULT_LEN + "}\\)\\)";
    private static final String DISMAX_QUERY_PATTERN = "\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\)";
    private static final String DISMAX_VALUE_PATTERN_FORMAT = "\\(((\\(|\\s)" + "\\+" + DISMAX_QUERY_PATTERN + "){%s}\\)\\)";
    private static final String TERM_QUERY_PATTERN_FORMAT = "\\(\\(%s:\"%s\"\\)\\^=%s\\.0\\)";
    private static final String MAIN_QUERY_PATTERN = "\\(" + VS_QUERY_PATTERN + " " + DISMAX_QUERY_PATTERN + "\\)";
    private static final Pattern DISMAX_NAME_PATTERN = Pattern.compile("dismax\\d+");
    private final SemanticTransformer<SQPBoostingStrategyConfig> transformer = new SQPBoostingStrategyTransformer();
    private final SQPBoostingStrategyConfig config = new SQPBoostingStrategyConfig();

    @Before
    public void initVectorSearchQuery() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String[] results = new String[VS_RESULT_LEN];
        for (int i = 0; i < results.length; i++) {
            long id = random.nextLong(Long.MAX_VALUE);
            float score = random.nextFloat();
            results[i] = String.format("%d^%.6f", id, score);
        }
        String vectorSearchQuery = "(" + String.join(" OR ", results) + ")";
        config.setVectorSearchQuery(vectorSearchQuery);
    }

    @Test
    public void test_unrecognizedGraph() {
        Map<String, String> query = transformer.transform(config, createGraph(
                new SemanticEdge("jazz"),
                new SemanticEdge("music")
        ));

        String mainDismaxName = assertAndGetMainQuery(query, 2);
        assertAndGetDismax(query, mainDismaxName, 0);
    }

    @Test
    public void test_partiallyRecognizedGraph_isNotAllowed() {
        SemanticEdge jazz = new SemanticEdge("jazz");
        SemanticEdge music = new SemanticEdge("music");
        music.getFields().add(getField("type", FieldMatchType.CONCEPT, 100));

        Map<String, String> query = transformer.transform(config, createGraph(jazz, music));

        String mainDismaxName = assertAndGetMainQuery(query, 2);
        assertAndGetDismax(query, mainDismaxName, 0);
    }

    @Test
    public void test_partiallyRecognizedGraph_isAllowed() {
        SemanticEdge jazz = new SemanticEdge("jazz");
        SemanticEdge music = new SemanticEdge("music");
        music.getFields().add(getField("type", FieldMatchType.CONCEPT, 100));
        config.setAllowPartialMatch(true);

        Map<String, String> query = transformer.transform(config, createGraph(jazz, music));

        String mainDismaxName = assertAndGetMainQuery(query, 3);
        List<String> dismaxNames = assertAndGetDismax(query, mainDismaxName, 1);

        String dismax = dismaxNames.get(0);
        assertTrue(query.get(dismax).matches(String.format(TERM_QUERY_PATTERN_FORMAT, "type_cpt", "music", 100)));
    }

    @Test
    public void test_recognizedGraph() {
        SemanticEdge jazz = new SemanticEdge("jazz");
        jazz.getFields().add(getField("genre", FieldMatchType.CONCEPT, 100));
        SemanticEdge music = new SemanticEdge("music");
        music.getFields().add(getField("type", FieldMatchType.CONCEPT, 100));

        Map<String, String> query = transformer.transform(config, createGraph(jazz, music));

        String mainDismaxName = assertAndGetMainQuery(query, 4);
        List<String> dismaxNames = assertAndGetDismax(query, mainDismaxName, 2);

        String dismax1 = dismaxNames.get(0);
        assertTrue(query.get(dismax1).matches(String.format(TERM_QUERY_PATTERN_FORMAT, "genre_cpt", "jazz", 100)));

        String dismax2 = dismaxNames.get(1);
        assertTrue(query.get(dismax2).matches(String.format(TERM_QUERY_PATTERN_FORMAT, "type_cpt", "music", 100)));
    }

    private SemanticGraph createGraph(SemanticEdge... edges) {
        SemanticGraph graph = new SemanticGraph();
        int i = 0;
        graph.addVertex(String.valueOf(i));
        while (i < edges.length) {
            graph.addVertex(String.valueOf(i + 1));
            graph.addEdge(String.valueOf(i), String.valueOf(i + 1), edges[i]);
            i++;
        }
        return graph;
    }

    private Field getField(String name, FieldMatchType type, float boost) {
        return Field.builder().name(name).type(type).boost(boost).build();
    }

    private String assertAndGetMainQuery(Map<String, String> query, int size) {
        assertEquals(size, query.size());
        String mainQuery = query.get("q");
        assertTrue(mainQuery.matches(MAIN_QUERY_PATTERN));
        Matcher matcher = DISMAX_NAME_PATTERN.matcher(mainQuery);
        assertTrue(matcher.find());
        return matcher.group();
    }

    private List<String> assertAndGetDismax(Map<String, String> query, String dismaxName, int subQueries) {
        String dismax = query.get(dismaxName);
        if (subQueries == 0) {
            assertEquals("", dismax);
        } else {
            assertTrue(dismax.matches(String.format(DISMAX_VALUE_PATTERN_FORMAT, subQueries)));
        }
        List<String> dismaxNames = new ArrayList<>();
        Matcher matcher = DISMAX_NAME_PATTERN.matcher(dismax);
        while (matcher.find()) {
            dismaxNames.add(matcher.group());
        }
        assertEquals(subQueries, dismaxNames.size());
        return dismaxNames;
    }

}
