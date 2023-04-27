package org.example;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * clean up the code
 * file input to generate graph
 * have some pointers and comments to explain structure
 * <p>
 * run simulation many times and get PoA average
 * true / false for debug mode?
 * tell Arya that I made a capacity graph for max flow
 *
 *
 * Rewrite CSV reader to make it my own
 * I used ChatGPT, should that be mentioned?
 */


public class Main {
    // Utility Variable. I cannot modify collection while looping through it, so this is a workaround
    private static ArrayList<Player> playersToRemove = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException, IOException {
        CustomGraph frame = new CustomGraph();
        Graph<String, FlowEdge> graph = frame.createGraphFromFiles("Vertices.csv", "Edges.csv");
        frame.visualize();
        double maxFlow = getMaxFlow(frame);

        //create players and relevant variables
        int numberOfPlayers = 100;
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Player> playersBacklog = new ArrayList<>();
        ArrayList<Player> playersInGame = new ArrayList<>();
        ArrayList<Player> playersDone = new ArrayList<>();
        createPlayers(numberOfPlayers, players, playersBacklog);

        int numberOfEpochs = 1000;
        double selfishFlow = 0;
        for(int t = 0; t < numberOfEpochs; t++) {
            ArrayList<FlowEdge> shortestPath = getShortestPath("Start", "End", graph); //all players that enter this epoch will have the same path
            if(!playersBacklog.isEmpty()) {
                addPlayers(playersBacklog, playersInGame, shortestPath, maxFlow, t);
            }

            movePlayers(playersInGame, playersDone, t);
            updateEdges(graph); //reset capacity and calculates new weight

            //if all players reached the end, then stop simulation
            if(playersDone.size() == players.size()) {
                System.out.println("Players Size: " + players.size());
                System.out.println("t: " + t);
                selfishFlow = (double) players.size() / t;
                break;
            }
        }

        double PoA = selfishFlow / maxFlow;

        System.out.println("Number of Players Done: " + playersDone.size());
        System.out.println("Max Flow: " + maxFlow);
        System.out.println("Selfish Flow: " + selfishFlow);
        System.out.println("Price of Anarchy: " + PoA);
    }



    private static void createPlayers(int numberOfPlayers, ArrayList<Player> players, ArrayList<Player> playersBacklog) {
        for(int i=0; i<numberOfPlayers; i++) {
            Player temp = new Player();
            players.add(temp);
            playersBacklog.add(temp);
        }
    }


    public static ArrayList<FlowEdge> getShortestPath(String Start, String End, Graph<String, FlowEdge> graph) {
        DijkstraShortestPath<String, FlowEdge> dijkstra = new DijkstraShortestPath<>(graph);
        List<FlowEdge> shortestPathList = dijkstra.getPath(Start, End).getEdgeList();

        return new ArrayList<>(shortestPathList);
    }

    private static void addPlayers(ArrayList<Player> playersBacklog, ArrayList<Player> playersInGame, ArrayList<FlowEdge> shortestPath, double maxFlow, int t) {
        for(int i=0; i<maxFlow; i++) {
            if(i >= playersBacklog.size()) {
                break;
            }
            Player player = playersBacklog.get(i);
            addSinglePlayer(player, playersInGame, (ArrayList<FlowEdge>) shortestPath.clone(), t);
        }
        playersBacklog.removeAll(playersToRemove);
        playersToRemove.clear();

        for(Player player : playersBacklog) {
            double rand = Math.random();
            if(rand < 0.1) {
                addSinglePlayer(player, playersInGame, (ArrayList<FlowEdge>) shortestPath.clone(), t);
            }
        }
        playersBacklog.removeAll(playersToRemove);
        playersToRemove.clear();

    }

    public static void addSinglePlayer(Player player, ArrayList<Player> playersInGame, ArrayList<FlowEdge> path, int t) {
        playersInGame.add(player);

        player.setTimeStarted(t);
        playersToRemove.add(player);

        player.setPath(path);
        handleVariablesForMovement(player, player.getEdge());
    }

    private static void movePlayers(ArrayList<Player> playersInGame, ArrayList<Player> playersDone, int t) {
        for(Player player : playersInGame) {
            moveSinglePlayer(player, t);
        }

        playersInGame.removeAll(playersToRemove);
//        System.out.println(playersToRemove);
        playersDone.addAll(playersToRemove);
        playersToRemove.clear();
    }

    public static void moveSinglePlayer(Player player, int t) {
        if(player.isInsideEdge()) {
            player.moveForward();
            if(player.finishedEdge()) {
                movePlayerToNextEdge(player, t);
            }
        } else {
            handleVariablesForMovement(player, player.getEdge());
        }

    }

    public static void movePlayerToNextEdge(Player player, int t) {
        FlowEdge oldEdge = player.removeOldEdge();
        oldEdge.getPlayersInEdge().remove(player);

        if(player.reachedEndOfGraph()) {
            handlePlayerReachingEnd(player, t);
            return;
        }

        FlowEdge nextEdge = player.getEdge();
        handleVariablesForMovement(player, nextEdge);
    }

    public static void handlePlayerReachingEnd(Player player, int t) {
        playersToRemove.add(player);
        player.calculateTimeTotal(t);
    }

    public static void handleVariablesForMovement(Player player, FlowEdge nextEdge) {
        player.setPosition(nextEdge);

        //checks if there's any room available for the player to join the edge
        if (nextEdge.isOpen() && player.canLeaveQueue()) {
            nextEdge.getPlayersInEdge().add(player);
            nextEdge.getQueue().remove(player);
            nextEdge.decrementCapacity();
            player.setRemainingTimeForEdge(nextEdge.getActualWeight());
        } else if(!player.isInQueue()) {
            nextEdge.getQueue().add(player); //if no space is available, the player waits in a queue
            player.setRemainingTimeForEdge(-1);
        }
    }

    private static void updateEdges(Graph<String, FlowEdge> graph) {
        for(FlowEdge edge : graph.edgeSet()) {
            edge.setRemainingCapacity(edge.getCapacity());
            double newWeight = edge.calculateAggregateWeight();
            graph.setEdgeWeight(edge, newWeight);
        }
    }

    private static double getMaxFlow(CustomGraph frame) {
        Graph<String, FlowEdge> capacityGraph = frame.getCapacityGraph();

        EdmondsKarpMFImpl<String, FlowEdge> ek = new EdmondsKarpMFImpl<>(capacityGraph);
        return ek.calculateMaximumFlow("Start", "End");
    }
}