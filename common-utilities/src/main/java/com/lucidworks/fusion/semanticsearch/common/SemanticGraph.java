package com.lucidworks.fusion.semanticsearch.common;

import com.lucidworks.fusion.semanticsearch.common.model.SemanticEdge;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class SemanticGraph {
    private final Graph<String, SemanticEdge> graph = new DirectedMultigraph<>(SemanticEdge.class);

    public static SemanticGraph fromDot(String dot) {
        SemanticGraph graph = new SemanticGraph();
        DOTImporter<String, SemanticEdge> dotImporter = new DOTImporter<>();
        dotImporter.setVertexFactory(s -> s);
        dotImporter.addEdgeAttributeConsumer(SemanticEdge::consumeAttribute);
        dotImporter.importGraph(graph.graph, new StringReader(dot));
        return graph;
    }

    public String toDot() {
        DOTExporter<String, SemanticEdge> exporter = new DOTExporter<>();
        exporter.setEdgeAttributeProvider(SemanticEdge::provideAttribute);
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }

    public void addVertex(String vertex) {
        graph.addVertex(vertex);
    }

    public void addEdge(String source, String target, SemanticEdge edge) {
        graph.addEdge(source, target, edge);
    }

    public void addEdgeIfAbsent(String source, String target, SemanticEdge edge) {
        if (isAbsent(edge, graph.getAllEdges(source, target))) {
            graph.addEdge(source, target, edge);
        }
    }

    private boolean isAbsent(SemanticEdge addingEdge, Set<SemanticEdge> edges) {
        return edges.stream().noneMatch(edge -> edge.isEqualTo(addingEdge));
    }

    public List<GraphPath<String, SemanticEdge>> getAllPaths() {
        TopologicalOrderIterator<String, SemanticEdge> graphIterator = new TopologicalOrderIterator<>(graph);
        String startVertex = null;
        String endVertex = null;

        if (graphIterator.hasNext()) {
            startVertex = graphIterator.next();
            while (graphIterator.hasNext()) {
                endVertex = graphIterator.next();
            }
        }

        if (startVertex != null && endVertex != null) {
            AllDirectedPaths<String, SemanticEdge> paths = new AllDirectedPaths<>(graph);
            return paths.getAllPaths(startVertex, endVertex, true, null);
        }

        return emptyList();
    }

    public List<GraphPath<String, SemanticEdge>> getAllSubPaths() {
        Set<String> vertices = new HashSet<>();

        TopologicalOrderIterator<String, SemanticEdge> graphIterator = new TopologicalOrderIterator<>(graph);
        while (graphIterator.hasNext()) {
            vertices.add(graphIterator.next());
        }

        if (vertices.size() < 2) {
            return emptyList();
        }

        AllDirectedPaths<String, SemanticEdge> paths = new AllDirectedPaths<>(graph);
        return paths.getAllPaths(vertices, vertices, true, null).stream()
                .filter(path -> !path.getEdgeList().isEmpty())
                .collect(toList());
    }

    public List<GraphPath<String, SemanticEdge>> getAllSubPaths(int maxPathLength) {
        Set<String> vertices = graph.vertexSet();

        if (vertices.size() < 2) {
            return emptyList();
        }

        AllDirectedPaths<String, SemanticEdge> paths = new AllDirectedPaths<>(graph);
        return paths.getAllPaths(vertices, vertices, true, maxPathLength).stream()
                .filter(path -> !path.getEdgeList().isEmpty())
                .collect(toList());
    }

    public String getEdgeSource(SemanticEdge edge) {
        return graph.getEdgeSource(edge);
    }

    public String getEdgeTarget(SemanticEdge edge) {
        return graph.getEdgeTarget(edge);
    }

    public Set<SemanticEdge> getAllEdges(String source, String target) {
        return graph.getAllEdges(source, target);
    }

    public Set<SemanticEdge> edgeSet() {
        return graph.edgeSet();
    }

    public Set<SemanticEdge> outgoingEdgesOf(String vertex) {
        return graph.outgoingEdgesOf(vertex);
    }

    @Override
    public String toString() {
        return graph.toString();
    }

    public Set<SemanticEdge> edgesOf(String vertex) {
        return graph.edgesOf(vertex);
    }
}
