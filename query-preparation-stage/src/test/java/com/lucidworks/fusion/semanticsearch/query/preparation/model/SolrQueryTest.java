package com.lucidworks.fusion.semanticsearch.query.preparation.model;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SolrQueryTest {

    private static final String QUERY_STRING = "queryString";
    private static final String Q = "q";
    private static final String SORT = "sort";
    private static final String FQ = "fq";

    @Test
    public void testParam_q() {
        Query query = mock(Query.class);
        when(query.getLocalParams()).thenReturn(Collections.emptySet());
        when(query.toString()).thenReturn(QUERY_STRING);

        SolrQuery solrQuery = new SolrQuery(query);

        Map<String, String> queryParams = solrQuery.toMap();

        assertEquals(1, queryParams.size());
        assertEquals(QUERY_STRING, queryParams.get(Q));
    }

    @Test
    public void testParam_qWithLocalParams() {
        LocalParam localParam1 = new LocalParam("param1", "subQuery1");
        LocalParam localParam2 = new LocalParam("param2", "subQuery2");

        Set<LocalParam> localParams = new HashSet<>();
        localParams.add(localParam1);
        localParams.add(localParam2);

        Query query = mock(Query.class);
        when(query.getLocalParams()).thenReturn(localParams);
        when(query.toString()).thenReturn(QUERY_STRING);

        SolrQuery solrQuery = new SolrQuery(query);

        Map<String, String> queryParams = solrQuery.toMap();

        assertEquals(3, queryParams.size());
        assertEquals(QUERY_STRING, queryParams.get(Q));
        assertEquals(localParam1.getValue(), queryParams.get(localParam1.getName()));
        assertEquals(localParam2.getValue(), queryParams.get(localParam2.getName()));
    }

    @Test
    public void testParam_qWithSorting() {
        Query query = mock(Query.class);
        when(query.getLocalParams()).thenReturn(Collections.emptySet());
        when(query.toString()).thenReturn(QUERY_STRING);

        String regularPriceAsc = "regularPrice ASC";
        String albumLabelDesc = "albumLabel DESC";
        Set<String> sorting = new LinkedHashSet<>();
        sorting.add(regularPriceAsc);
        sorting.add(albumLabelDesc);

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setSort(sorting);

        Map<String, String> queryParams = solrQuery.toMap();

        assertEquals(2, queryParams.size());
        assertEquals(QUERY_STRING, queryParams.get(Q));
        assertEquals(String.join(", ", "score DESC", regularPriceAsc, albumLabelDesc),
                     queryParams.get(SORT));
    }

    @Test
    public void testParam_qWithFilters() {
        Query query = mock(Query.class);
        when(query.getLocalParams()).thenReturn(Collections.emptySet());
        when(query.toString()).thenReturn(QUERY_STRING);

        String regularPriceUnder10 = "regularPrice:[0 TO 10]";
        String typeMusic = "type:\"music\"";
        Set<String> filters = new LinkedHashSet<>();
        filters.add(regularPriceUnder10);
        filters.add(typeMusic);

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setFq(filters);

        Map<String, String> queryParams = solrQuery.toMap();

        assertEquals(2, queryParams.size());
        assertEquals(QUERY_STRING, queryParams.get(Q));
        assertEquals(String.join(" AND ", regularPriceUnder10, typeMusic),
                     queryParams.get(FQ));
    }

}