package org.example;

import com.mxgraph.layout.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxPerimeter;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import javax.swing.*;
import java.awt.*;

public class CustomGraph extends JFrame {
    private static final long serialVersionUID = 1L;

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

    public void visualize() {
        JGraphXAdapter<String, FlowEdge> jgxAdapter = new JGraphXAdapter<>(graph);
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

//        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
//        mxFastOrganicLayout layout = new mxFastOrganicLayout(jgxAdapter);
    }
}
