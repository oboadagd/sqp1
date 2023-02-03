package com.lucidworks.fusion.semanticsearch.query.preparation;

import com.lucidworks.fusion.semanticsearch.common.SemanticGraph;
import com.lucidworks.fusion.semanticsearch.common.base.SemanticTransformer;
import com.lucidworks.fusion.semanticsearch.common.model.Field;
import com.lucidworks.fusion.semanticsearch.common.model.FieldMatchType;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import com.lucidworks.fusion.semanticsearch.common.model.SemanticModifier;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.ExactMatch;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.ExactMatchConfig;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.RelaxedMatch;
import com.lucidworks.fusion.semanticsearch.query.preparation.stage.RelaxedMatchConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolrQueryPreparationTransformerTest {

    private static final String CONCEPT_FIELD_SUFFIX = "_cpt";
    private final SemanticTransformer<SolrQueryPreparationConfig> transformer = new SolrQueryPreparationTransformer();
    private final SemanticGraph graph = new SemanticGraph();

    @Before
    public void initGraph() {
        graph.addVertex("0");
        graph.addVertex("1");
        graph.addVertex("2");

        SemanticEdge jazz = new SemanticEdge("jazz");
        Set<Field> jazzFields = jazz.getFields();
        jazzFields.add(getField("genre", FieldMatchType.CONCEPT, 100));
        jazzFields.add(getField("albumLabel", FieldMatchType.TEXT, 10));
        jazz.getModifiers().add(SemanticModifier.HYP);
        graph.addEdge("0", "1", jazz);

        SemanticEdge acidJazz = new SemanticEdge("acid jazz");
        Set<Field> acidJazzFields = acidJazz.getFields();
        acidJazzFields.add(getField("genre", FieldMatchType.CONCEPT, 100));
        acidJazzFields.add(getField("albumLabel", FieldMatchType.TEXT, 10));
        graph.addEdge("0", "1", acidJazz);

        SemanticEdge music = new SemanticEdge("music");
        Set<Field> musicFields = music.getFields();
        musicFields.add(getField("type", FieldMatchType.CONCEPT, 100));
        musicFields.add(getField("albumLabel", FieldMatchType.TEXT, 10));
        graph.addEdge("1", "2", music);
    }

    @Test
    public void testRelaxedMatch_defaultConfig() {
        RelaxedMatchConfig relaxedMatchConfig = new RelaxedMatchConfig(CONCEPT_FIELD_SUFFIX,
                                                                       true,
                                                                       true,
                                                                       false);
        RelaxedMatch relaxedMatch = new RelaxedMatch(relaxedMatchConfig);
        SolrQueryPreparationConfig config = new SolrQueryPreparationConfig();
        config.setSearchStage(relaxedMatch);
        Map<String, String> query = transformer.transform(config, graph);

        Pattern pattern = Pattern.compile("dismax\\d+");

        String mainQuery = query.get("q");
        Assert.assertTrue(mainQuery.matches("_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\""));

        Matcher matcher = pattern.matcher(mainQuery);
        Assert.assertTrue(matcher.find());

        String mainDismaxName = matcher.group();
        String mainDismax = query.get(mainDismaxName);
        Assert.assertTrue(mainDismax.matches("\\(\\(\\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\) \\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\)\\) \\| \\(\\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\) \\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\)\\)\\)"));

        matcher = pattern.matcher(mainDismax);
        Assert.assertTrue(matcher.find());
        String dismax1 = matcher.group(0);
        Assert.assertTrue(query.get(dismax1).matches("\\(\\(genre_cpt:\"jazz\"\\)\\^=100\\.0 \\| \\(albumLabel_cpt:\"jazz\"\\)\\^=10\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax2 = matcher.group(0);
        Assert.assertTrue(query.get(dismax2).matches("\\(\\(type_cpt:\"music\"\\)\\^=100\\.0 \\| \\(albumLabel_cpt:\"music\"\\)\\^=10\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax3 = matcher.group(0);
        Assert.assertTrue(query.get(dismax3).matches("\\(\\(genre_cpt:\"acid jazz\"\\)\\^=100\\.0 \\| \\(albumLabel_cpt:\"acid jazz\"\\)\\^=10\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax4 = matcher.group(0);

        Assert.assertEquals(dismax2, dismax4);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void testRelaxedMatch_onlyTopPriorityField() {
        RelaxedMatchConfig relaxedMatchConfig = new RelaxedMatchConfig(CONCEPT_FIELD_SUFFIX,
                                                                       true,
                                                                       true,
                                                                       true);
        RelaxedMatch relaxedMatch = new RelaxedMatch(relaxedMatchConfig);
        SolrQueryPreparationConfig config = new SolrQueryPreparationConfig();
        config.setSearchStage(relaxedMatch);
        Map<String, String> query = transformer.transform(config, graph);

        Pattern pattern = Pattern.compile("dismax\\d+");

        String mainQuery = query.get("q");
        Assert.assertTrue(mainQuery.matches("_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\""));

        Matcher matcher = pattern.matcher(mainQuery);
        Assert.assertTrue(matcher.find());

        String mainDismaxName = matcher.group();
        String mainDismax = query.get(mainDismaxName);
        Assert.assertTrue(mainDismax.matches("\\(\\(\\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\) \\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\)\\) \\| \\(\\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\) \\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\)\\)\\)"));

        matcher = pattern.matcher(mainDismax);
        Assert.assertTrue(matcher.find());
        String dismax1 = matcher.group(0);
        Assert.assertTrue(query.get(dismax1).matches("\\(\\(genre_cpt:\"jazz\"\\)\\^=100\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax2 = matcher.group(0);
        Assert.assertTrue(query.get(dismax2).matches("\\(\\(type_cpt:\"music\"\\)\\^=100\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax3 = matcher.group(0);
        Assert.assertTrue(query.get(dismax3).matches("\\(\\(genre_cpt:\"acid jazz\"\\)\\^=100\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax4 = matcher.group(0);

        Assert.assertEquals(dismax2, dismax4);
        Assert.assertFalse(matcher.find());
    }

    @Test
    public void testRelaxedMatch_linguisticsForbidden() {
        RelaxedMatchConfig relaxedMatchConfig = new RelaxedMatchConfig(CONCEPT_FIELD_SUFFIX,
                                                                       false,
                                                                       true,
                                                                       false);
        RelaxedMatch relaxedMatch = new RelaxedMatch(relaxedMatchConfig);
        SolrQueryPreparationConfig config = new SolrQueryPreparationConfig();
        config.setSearchStage(relaxedMatch);
        Map<String, String> query = transformer.transform(config, graph);

        Pattern pattern = Pattern.compile("dismax\\d+");

        String mainQuery = query.get("q");
        Assert.assertTrue(mainQuery.matches("_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\""));

        Matcher matcher = pattern.matcher(mainQuery);
        Assert.assertTrue(matcher.find());

        String mainDismaxName = matcher.group();
        String mainDismax = query.get(mainDismaxName);
        Assert.assertTrue(mainDismax.matches("\\(\\(\\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\) \\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\)\\)\\)"));

        matcher = pattern.matcher(mainDismax);
        Assert.assertTrue(matcher.find());
        String dismax1 = matcher.group(0);
        Assert.assertTrue(query.get(dismax1).matches("\\(\\(genre_cpt:\"acid jazz\"\\)\\^=100\\.0 \\| \\(albumLabel_cpt:\"acid jazz\"\\)\\^=10\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax2 = matcher.group(0);
        Assert.assertTrue(query.get(dismax2).matches("\\(\\(type_cpt:\"music\"\\)\\^=100\\.0 \\| \\(albumLabel_cpt:\"music\"\\)\\^=10\\.0\\)"));

        Assert.assertFalse(matcher.find());
    }

    @Test
    public void testExactMatch() {
        ExactMatchConfig exactMatchConfig = new ExactMatchConfig(CONCEPT_FIELD_SUFFIX);
        ExactMatch exactMatch = new ExactMatch(exactMatchConfig);
        SolrQueryPreparationConfig config = new SolrQueryPreparationConfig();
        config.setSearchStage(exactMatch);
        Map<String, String> query = transformer.transform(config, graph);

        Pattern pattern = Pattern.compile("dismax\\d+");

        String mainQuery = query.get("q");
        Assert.assertTrue(mainQuery.matches("_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\""));

        Matcher matcher = pattern.matcher(mainQuery);
        Assert.assertTrue(matcher.find());

        String mainDismaxName = matcher.group();
        String mainDismax = query.get(mainDismaxName);
        Assert.assertTrue(mainDismax.matches("\\(\\(\\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\) \\+\\(_query_:\"\\{!maxscore tie=0\\.0 v=\\$dismax\\d+}\"\\)\\)\\)"));

        matcher = pattern.matcher(mainDismax);
        Assert.assertTrue(matcher.find());
        String dismax1 = matcher.group(0);
        Assert.assertTrue(query.get(dismax1).matches("\\(\\(genre_cpt:\"acid jazz\"\\)\\^=100\\.0\\)"));

        Assert.assertTrue(matcher.find());
        String dismax2 = matcher.group(0);
        Assert.assertTrue(query.get(dismax2).matches("\\(\\(type_cpt:\"music\"\\)\\^=100\\.0\\)"));

        Assert.assertFalse(matcher.find());
    }

    private Field getField(String name, FieldMatchType type, float boost) {
        return Field.builder().name(name).type(type).boost(boost).build();
    }
}