package org.example;

import com.mxgraph.layout.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomGraph extends JFrame {
    private static final long serialVersionUID = 1L;

    Graph<String, FlowEdge> graph;
    Graph<String, FlowEdge> capacityGraph;

    public CustomGraph() {
//        this.graph = new SimpleDirectedWeightedGraph<>(FlowEdge.class);
//
//        //create vertices and assign them
//        String Start = "Start";
//        String A = "A";
//        String B = "B";
//        String C = "C";
//        String End = "End";
//        this.graph.addVertex(Start);
//        this.graph.addVertex(A);
//        this.graph.addVertex(B);
//        this.graph.addVertex(C);
//        this.graph.addVertex(End);
//
//        //create edges
//        FlowEdge E_Start_A = this.graph.addEdge(Start, A);
//        this.graph.setEdgeWeight(E_Start_A, 3);
//        E_Start_A.setActualWeight(3);
//        E_Start_A.setCapacity(7);
//
//        FlowEdge E_Start_C = this.graph.addEdge(Start, C);
//        this.graph.setEdgeWeight(E_Start_C, 2);
//        E_Start_C.setActualWeight(2);
//        E_Start_C.setCapacity(10);
//
//        FlowEdge E_A_B = this.graph.addEdge(A, B);
//        this.graph.setEdgeWeight(E_A_B, 2);
//        E_A_B.setActualWeight(2);
//        E_A_B.setCapacity(7);
//
//        FlowEdge E_B_End = this.graph.addEdge(B, End);
//        this.graph.setEdgeWeight(E_B_End, 1);
//        E_B_End.setActualWeight(1);
//        E_B_End.setCapacity(5);
//
//        FlowEdge E_C_B = this.graph.addEdge(C, B);
//        this.graph.setEdgeWeight(E_C_B, 2);
//        E_C_B.setActualWeight(2);
//        E_C_B.setCapacity(3);
//
//        FlowEdge E_C_End = this.graph.addEdge(C, End);
//        this.graph.setEdgeWeight(E_C_End, 5);
//        E_C_End.setActualWeight(5);
//        E_C_End.setCapacity(10);
    }

//    public Graph<String, FlowEdge> getGraph() {
//        return this.graph;
//    }

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
        System.out.println(this.graph);
        return this.graph;
    }

    public void visualize() {
        JGraphXAdapter<String, FlowEdge> jgxAdapter = new JGraphXAdapter<>(this.graph);
        jgxAdapter.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_NOLABEL, "1");
        jgxAdapter.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_SHAPE, "1");
        jgxAdapter.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTSIZE, "13");

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
}
