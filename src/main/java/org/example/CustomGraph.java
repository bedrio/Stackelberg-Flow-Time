package org.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class CustomGraph {
    Graph<String, FlowEdge> graph;

    public CustomGraph() {
        this.graph = new SimpleDirectedWeightedGraph<>(FlowEdge.class);

        //create vertices and assign them
        String Start = "Start";
        String A = "A";
        String B = "B";
        String C = "C";
        String End = "End";
        graph.addVertex(Start);
        graph.addVertex(A);
        graph.addVertex(B);
        graph.addVertex(C);
        graph.addVertex(End);

        //create edges
        FlowEdge E_Start_A = graph.addEdge(Start, A);
        graph.setEdgeWeight(E_Start_A, 3);
        E_Start_A.setActualWeight(3);
        E_Start_A.setCapacity(7);

        FlowEdge E_Start_C = graph.addEdge(Start, C);
        graph.setEdgeWeight(E_Start_C, 2);
        E_Start_C.setActualWeight(2);
        E_Start_C.setCapacity(10);

        FlowEdge E_A_B = graph.addEdge(A, B);
        graph.setEdgeWeight(E_A_B, 2);
        E_A_B.setActualWeight(2);
        E_A_B.setCapacity(7);

        FlowEdge E_B_End = graph.addEdge(B, End);
        graph.setEdgeWeight(E_B_End, 1);
        E_B_End.setActualWeight(1);
        E_B_End.setCapacity(5);

        FlowEdge E_C_B = graph.addEdge(C, B);
        graph.setEdgeWeight(E_C_B, 2);
        E_C_B.setActualWeight(2);
        E_C_B.setCapacity(3);

        FlowEdge E_C_End = graph.addEdge(C, End);
        graph.setEdgeWeight(E_C_End, 5);
        E_C_End.setActualWeight(5);
        E_C_End.setCapacity(10);
    }

    public Graph<String, FlowEdge> getGraph() {
        return graph;
    }
}
