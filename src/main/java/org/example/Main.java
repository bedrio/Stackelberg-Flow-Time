package org.example;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Graph<String, FlowEdge> g = new SimpleDirectedWeightedGraph<>(FlowEdge.class);

        //create vertices and assign them
        String Start = "Start";
        String A = "A";
        String B = "B";
        String C = "C";
        String End = "End";
        g.addVertex(Start);
        g.addVertex(A);
        g.addVertex(B);
        g.addVertex(C);
        g.addVertex(End);

        //create edges
        FlowEdge E_Start_A = g.addEdge(Start, A);
        g.setEdgeWeight(E_Start_A, 3);
        E_Start_A.setActualWeight(3);
        E_Start_A.setCapacity(7);

        FlowEdge E_Start_C = g.addEdge(Start, C);
        g.setEdgeWeight(E_Start_C, 2);
        E_Start_C.setActualWeight(2);
        E_Start_C.setCapacity(10);

        FlowEdge E_A_B = g.addEdge(A, B);
        g.setEdgeWeight(E_A_B, 2);
        E_A_B.setActualWeight(2);
        E_A_B.setCapacity(7);

        FlowEdge E_B_End = g.addEdge(B, End);
        g.setEdgeWeight(E_B_End, 1);
        E_B_End.setActualWeight(1);
        E_B_End.setCapacity(5);

        FlowEdge E_C_B = g.addEdge(C, B);
        g.setEdgeWeight(E_C_B, 2);
        E_C_B.setActualWeight(2);
        E_C_B.setCapacity(3);

        FlowEdge E_C_End = g.addEdge(C, End);
        g.setEdgeWeight(E_C_End, 5);
        E_C_End.setActualWeight(5);
        E_C_End.setCapacity(10);

        //create players
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Player> playersBacklog = new ArrayList<>();
        for(int i=0; i<20; i++) {
            players.add(new Player());
            playersBacklog.add(new Player());
        }



        //TODO don't I need to have strategy update after each player??
        //TODO new flow definition from Arya
        ArrayList<Player> playersInGame = new ArrayList<>();
        ArrayList<Player> playersToRemove = new ArrayList<>(); // I cannot modify collection while looping through it, so this is a workaround
        for(int t = 0; t < 100; t++) {
            //shortest path remains constant in this single epoch
            DijkstraShortestPath<String, FlowEdge> dijkstra = new DijkstraShortestPath<>(g);
            List<FlowEdge> shortestPathList = dijkstra.getPath(Start, End).getEdgeList();
            ArrayList<FlowEdge> shortestPath = new ArrayList<>(shortestPathList);
            // double shortestPathWeight = dijkstra.getPathWeight(Start, End);
//             System.out.println(shortestPath);
            // System.out.println("Shortest path weight: " + shortestPathWeight);

            //create players and add them
            //TODO number of players entering must be at least max flow
            for(Player player : playersBacklog) {
                double rand = Math.random();
                if(rand < 0.3) {
                    playersInGame.add(player); //TODO put in function
                    addPlayerToGame(player, playersToRemove, (ArrayList<FlowEdge>) shortestPath.clone(), t);
                }
            }
            playersBacklog.removeAll(playersToRemove);
            playersToRemove.clear();

            //move players
            for(Player player : playersInGame) {
                player.iterateTime();
                if(player.getRemainingTimeForEdge() == 0) {
                    movePlayerToNextEdge(player, playersToRemove, t);
                }
            }
           playersInGame.removeAll(playersToRemove);
           playersToRemove.clear();

            //TODO Update Aggregate Weights
            for(FlowEdge edge : g.edgeSet()) {
                double newWeight = edge.calculateAggregateWeight();
                g.setEdgeWeight(edge, newWeight);
            }
        }


//        EdmondsKarpMFImpl<String, FlowEdge> ek = new EdmondsKarpMFImpl<>(g);
//        double maxFlow = ek.calculateMaximumFlow(Start, End);
//        System.out.println(maxFlow);
    }

    public static void addPlayerToGame(Player player, ArrayList<Player> playersToRemove, ArrayList<FlowEdge> path, int t) {
        player.setTimeStarted(t);
        playersToRemove.add(player);

        //TODO this assumes that "Start" is the beginning node.
        // does this cover all variables?
        player.setPath(path);
        movePlayerToNextEdge(player, playersToRemove, t);
    }

    public static void movePlayerToNextEdge(Player player, ArrayList<Player> playersToRemove, int t) {
        player.removeOldEdge();
        if(player.reachedEnd()) {
            handlePlayerReachingEnd(player, playersToRemove, t);
        }

        System.out.println(player.getPath());
        FlowEdge nextEdge = player.getEdge();
        handleVariablesForMovement(player, nextEdge);
    }

    //TODO Anything else?
    public static void handlePlayerReachingEnd(Player player, ArrayList<Player> playersToRemove, int t) {
        playersToRemove.add(player);
        player.calculateTimeTotal(t);
    }

    public static void handleVariablesForMovement(Player player, FlowEdge nextEdge) {
        //checks if there's any room available for the player to join the edge
        if (nextEdge.isOpen()) {
            nextEdge.getPlayersInEdge().add(player);
            player.setRemainingTimeForEdge(nextEdge.getActualWeight());
        } else {
            //TODO check if this is working properly or is defaulting to null sizes
            nextEdge.getQueue().add(player); //if no space is available, the player waits in a queue
        }

        player.setPosition(nextEdge);
    }
}