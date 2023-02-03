package com.lucidworks.fusion.semanticsearch.query.preparation;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.model.Field;
import com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.query.preparation.ComprehensionProcessor.Comprehensions;
import com.lucidworks.fusion.semanticsearch.query.preparation.ComprehensionProcessor.OverriddenGraph;
import org.jgrapht.GraphPath;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType.CONCEPT;
import static com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType.TEXT;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.FILTER;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.HYP;
import static com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier.SORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComprehensionProcessorTest {

    private final ComprehensionProcessor processor = new ComprehensionProcessor();

    @Test
    public void testCheapMusic() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("1", "2", music);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       true,
                                                                       true,
                                                                       false);

        Comprehensions comprehensions = result.getComprehensions();
        assertEquals(1, comprehensions.size());

        assertTrue(comprehensions.getSorting().contains(cheapSort.getToken()));

        List<GraphPath<String, SemanticEdge>> allPaths = result.getGraph().getAllPaths();
        assertEquals(1, allPaths.size());

        List<SemanticEdge> path = allPaths.get(0).getEdgeList();
        assertEquals(1, path.size());

        SemanticEdge edge = path.get(0);
        assertTrue(music.isEqualTo(edge));
    }

    @Test
    public void testCheapMusic_unrecognized() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("1", "2", music);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       false,
                                                                       false,
                                                                       false);

        Comprehensions comprehensions = result.getComprehensions();
        assertEquals(1, comprehensions.size());

        assertTrue(comprehensions.getSorting().contains(cheapSort.getToken()));

        List<GraphPath<String, SemanticEdge>> allPaths = result.getGraph().getAllPaths();
        assertEquals(1, allPaths.size());

        List<SemanticEdge> path = allPaths.get(0).getEdgeList();
        assertEquals(1, path.size());

        SemanticEdge edge = path.get(0);
        assertTrue(music.isEqualTo(edge));
    }

    @Test
    public void testCheapMusic_musicFilter() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("1", "2", music);

        SemanticEdge musicFilter = new SemanticEdge("type:\"music\"");
        musicFilter.getModifiers().add(FILTER);
        graph.addEdgeIfAbsent("0", "2", musicFilter);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       true,
                                                                       true,
                                                                       false);

        Comprehensions comprehensions = result.getComprehensions();
        assertEquals(1, comprehensions.size());

        assertTrue(comprehensions.getSorting().contains(cheapSort.getToken()));

        List<GraphPath<String, SemanticEdge>> allPaths = result.getGraph().getAllPaths();
        assertEquals(1, allPaths.size());

        List<SemanticEdge> path = allPaths.get(0).getEdgeList();
        assertEquals(1, path.size());

        SemanticEdge edge = path.get(0);
        assertTrue(music.isEqualTo(edge));
    }

    @Test
    public void testCheapMusicUnder10Dollars_skipText() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");
        graph.addVertex("4");
        graph.addVertex("5");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("1", "2", music);

        SemanticEdge under = new SemanticEdge("under");
        Set<Field> underFields = under.getFields();
        underFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("2", "3", under);

        SemanticEdge ten = new SemanticEdge("10");
        Set<Field> tenFields = ten.getFields();
        tenFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("3", "4", ten);

        SemanticEdge dollars = new SemanticEdge("dollar");
        Set<Field> dollarsFields = ten.getFields();
        dollarsFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("4", "5", dollars);

        SemanticEdge under10dollarsFilter = new SemanticEdge("regularPrice:[0 TO 10]");
        under10dollarsFilter.getModifiers().add(FILTER);
        graph.addEdgeIfAbsent("2", "5", under10dollarsFilter);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       true,
                                                                       true,
                                                                       false);

        Comprehensions comprehensions = result.getComprehensions();
        assertEquals(2, comprehensions.size());

        assertTrue(comprehensions.getSorting().contains(cheapSort.getToken()));

        assertTrue(comprehensions.getFilters().contains(under10dollarsFilter.getToken()));

        List<GraphPath<String, SemanticEdge>> allPaths = result.getGraph().getAllPaths();
        assertEquals(1, allPaths.size());

        List<SemanticEdge> path = allPaths.get(0).getEdgeList();
        assertEquals(1, path.size());

        SemanticEdge edge = path.get(0);
        assertTrue(music.isEqualTo(edge));
    }

    @Test
    public void testCheapMusicUnder10Dollars_useText() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");
        graph.addVertex("4");
        graph.addVertex("5");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("1", "2", music);

        SemanticEdge under = new SemanticEdge("under");
        Set<Field> underFields = under.getFields();
        underFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("2", "3", under);

        SemanticEdge ten = new SemanticEdge("10");
        Set<Field> tenFields = ten.getFields();
        tenFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("3", "4", ten);

        SemanticEdge dollars = new SemanticEdge("dollar");
        Set<Field> dollarsFields = ten.getFields();
        dollarsFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("4", "5", dollars);

        SemanticEdge under10dollarsFilter = new SemanticEdge("regularPrice:[0 TO 10]");
        under10dollarsFilter.getModifiers().add(FILTER);
        graph.addEdgeIfAbsent("2", "5", under10dollarsFilter);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       false,
                                                                       true,
                                                                       false);

        assertTrue(result.getComprehensions().isEmpty());
        assertEquals(graph, result.getGraph());
    }

    @Test
    public void testCheapMusic_comprehensionOverlap() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge inexpensiveSort = new SemanticEdge("inexpensive ASC");
        inexpensiveSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", inexpensiveSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("1", "2", music);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       true,
                                                                       true,
                                                                       false);

        assertTrue(result.getComprehensions().isEmpty());
        assertEquals(graph, result.getGraph());
    }

    @Test
    public void testCheapTrickMusic_conceptOverlap() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge trick = new SemanticEdge("trick");
        Set<Field> trickFields = trick.getFields();
        trickFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("1", "2", trick);

        SemanticEdge cheapTrick = new SemanticEdge("cheap trick");
        Set<Field> cheapTrickFields = cheapTrick.getFields();
        cheapTrickFields.add(getField("artistName", CONCEPT));
        graph.addEdgeIfAbsent("0", "2", cheapTrick);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("2", "3", music);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       true,
                                                                       true,
                                                                       false);

        assertTrue(result.getComprehensions().isEmpty());
        assertEquals(graph, result.getGraph());
    }

    @Test
    public void testCheapTrickMusic_textOverlap_useText() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge trick = new SemanticEdge("trick");
        Set<Field> trickFields = trick.getFields();
        trickFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("1", "2", trick);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("2", "3", music);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       false,
                                                                       false,
                                                                       false);

        assertTrue(result.getComprehensions().isEmpty());
        assertEquals(graph, result.getGraph());
    }

    @Test
    public void testCheapJazzMusic_textOverlap_skipText() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");

        SemanticEdge cheapSort = new SemanticEdge("cheap ASC");
        cheapSort.getModifiers().add(SORT);
        graph.addEdgeIfAbsent("0", "1", cheapSort);

        SemanticEdge cheap = new SemanticEdge("cheap");
        Set<Field> cheapFields = cheap.getFields();
        cheapFields.add(getField("artistName", TEXT));
        graph.addEdgeIfAbsent("0", "1", cheap);

        SemanticEdge jazz = new SemanticEdge("jazz");
        Set<Field> jazzFields = jazz.getFields();
        jazzFields.add(getField("genre", CONCEPT));
        graph.addEdgeIfAbsent("1", "2", jazz);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("2", "3", music);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       true,
                                                                       false,
                                                                       false);

        Comprehensions comprehensions = result.getComprehensions();
        assertEquals(1, comprehensions.size());

        assertTrue(comprehensions.getSorting().contains(cheapSort.getToken()));

        List<GraphPath<String, SemanticEdge>> allPaths = result.getGraph().getAllPaths();
        assertEquals(1, allPaths.size());

        List<SemanticEdge> path = allPaths.get(0).getEdgeList();
        assertEquals(2, path.size());
        assertTrue(jazz.isEqualTo(path.get(0)));
        assertTrue(music.isEqualTo(path.get(1)));
    }

    @Test
    public void testAcidJazzMusic() {
        SemanticGraph graph = new SemanticGraph();
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");

        SemanticEdge jazz = new SemanticEdge("jazz");
        Set<Field> jazzFields = jazz.getFields();
        jazzFields.add(getField("genre", CONCEPT));
        jazzFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("0", "1", jazz);

        SemanticEdge acidJazz = new SemanticEdge("acid jazz");
        Set<Field> acidJazzFields = acidJazz.getFields();
        acidJazzFields.add(getField("genre", CONCEPT));
        acidJazzFields.add(getField("albumLabel", TEXT));
        acidJazz.getModifiers().add(HYP);
        graph.addEdgeIfAbsent("0", "1", acidJazz);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", CONCEPT));
        musicFields.add(getField("albumLabel", TEXT));
        graph.addEdgeIfAbsent("1", "2", music);

        OverriddenGraph result = processor.splitGraphAndComprehensions(graph,
                                                                       true,
                                                                       true,
                                                                       false);

        assertTrue(result.getComprehensions().isEmpty());
        assertEquals(graph, result.getGraph());
    }

    private Field getField(String name, FieldMatchType type) {
        return Field.builder().name(name).type(type).build();
    }
}