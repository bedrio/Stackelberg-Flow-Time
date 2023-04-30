package org.example;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomGraph extends JFrame {
    private static final long serialVersionUID = 1L;

    Graph<String, FlowEdge> graph;
    Graph<String, FlowEdge> capacityGraph;

    public CustomGraph() {}

    public CustomGraph(List<GraphPath<String, FlowEdge>> paths) {
        this.graph = new SimpleDirectedWeightedGraph<>(FlowEdge.class);

        for(GraphPath<String, FlowEdge> path : paths) {
            for(String vertex : path.getVertexList()) {
                graph.addVertex(vertex);
            }

            for(FlowEdge edge : path.getEdgeList()) {
                graph.addEdge(edge.getSource(), edge.getTarget(), edge);
                graph.setEdgeWeight(edge, edge.getCapacity());
            }
        }
    }

    public Graph<String, FlowEdge> createGraphFromFiles(String VerticesFilePath, String EdgeFilePath) throws IOException {
        this.graph = new SimpleDirectedWeightedGraph<>(FlowEdge.class);

        CSVReader reader = new CSVReader();

        ArrayList<Map<String, String>> vertices = reader.readCSV(VerticesFilePath);
        for(Map<String, String> vertex : vertices) {
            String VertexName = vertex.get("Vertex");
            this.graph.addVertex(VertexName);
        }

        ArrayList<Map<String, String>> edges = reader.readCSV(EdgeFilePath);
        for(Map<String, String> edge : edges) {
            String Source = edge.get("Source");
            String Target = edge.get("Target");

            String x = edge.get("Delay");
            double delay = Double.parseDouble(x);
            double capacity = Double.parseDouble(edge.get("Capacity"));

            FlowEdge tempEdge = this.graph.addEdge(Source, Target);
            this.graph.setEdgeWeight(tempEdge, delay);
            tempEdge.setActualWeight(delay);
            tempEdge.setCapacity(capacity);
        }
        return this.graph;
    }

    public void visualize() {
        JGraphXAdapter<String, FlowEdge> jgxAdapter = new JGraphXAdapter<>(this.graph);

        HashMap<FlowEdge, mxICell> edgesMap = jgxAdapter.getEdgeToCellMap();
        for (var entry : edgesMap.entrySet()) {
            entry.getValue().setValue(entry.getKey().getCapacity());
        }

        HashMap<String, mxICell> VertexMap = jgxAdapter.getVertexToCellMap();
        for (var entry : VertexMap.entrySet()) {
            mxRectangle x = new mxRectangle(0, 0, 100, 100);
            jgxAdapter.resizeCell(entry.getValue(), x);
        }

//        jgxAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
        jgxAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#A3BB00");
//        jgxAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.LABEL, "#A3BB00");
        jgxAdapter.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_SHAPE, "1");
        jgxAdapter.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTSIZE, "20");


        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        mxCompactTreeLayout layout = new mxCompactTreeLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
        add(graphComponent);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public Graph<String, FlowEdge> getCapacityGraph() {
        this.capacityGraph = this.graph;

        for(FlowEdge edge : capacityGraph.edgeSet()) {
            capacityGraph.setEdgeWeight(edge, edge.getCapacity());
        }

        return capacityGraph;
    }

    public Graph<String, FlowEdge> getGraph() {
        return graph;
    }
}
