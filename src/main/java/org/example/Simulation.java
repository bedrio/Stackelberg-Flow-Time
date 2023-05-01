package org.example;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Simulation {
    // Utility Variable. I cannot modify collection while looping through it, so this is a workaround
    private ArrayList<Player> playersToRemove = new ArrayList<>();
    private Graph<String, FlowEdge> graph;
    private CustomGraph builder;
    private ArrayList<Player> players;
    private ArrayList<Player> playersBacklog;
    private ArrayList<Player> playersInGame;
    private ArrayList<Player> playersDone;

    public Simulation(String verticesFileName, String edgesFileName) throws IOException {
        this.builder = new CustomGraph();
        this.graph = builder.createGraphFromFiles(verticesFileName, edgesFileName);

        //Very helpful for debugging
        this.players = new ArrayList<>(); //all players
        this.playersBacklog = new ArrayList<>(); //players that have not entered the game yet
        this.playersInGame = new ArrayList<>(); //players that are in the game
        this.playersDone = new ArrayList<>(); //players that finished the game
    }

    public double simulate(int numberOfPlayers, int numberOfEpochs, int minExtraPlayers, int maxExtraPlayers, boolean visualize, boolean simpleDebug) throws IOException {
        if(visualize) {
            builder.visualize();
        }
        createPlayers(numberOfPlayers);

        AllDirectedPaths<String, FlowEdge> allDirectedPaths = new AllDirectedPaths<>(graph);
        List<GraphPath<String, FlowEdge>> flowPaths = allDirectedPaths.getAllPaths("Start", "End",false, 999999);
        List<GraphPath<String, FlowEdge>> flowPathsReached = new ArrayList<>();

        double maxFlow = getMaxFlow(builder.getCapacityGraph());
        double maxFlowOverTime = 0;
        double maxFlowIncrementValue = 0;
        double selfishFlow = 0;
        int t = 0;
        for(; t < numberOfEpochs; t++) {
            // calculate number of extra players
            if(!playersInGame.isEmpty()) {
                boolean resetCapacity = true;
                movePlayers(t);
                updateEdges(resetCapacity);
            }

            int extraPlayers = new Random().nextInt(maxExtraPlayers + 1);



            for(int i=0; i < (maxFlow + extraPlayers); i++) {
                if(playersBacklog.isEmpty()) {
                    break;
                }
                Player player = playersBacklog.remove(0);
                ArrayList<FlowEdge> shortestPath = getShortestPath("Start", "End"); //all players that enter this epoch will have the same path

                addSinglePlayer(player, (ArrayList<FlowEdge>) shortestPath.clone(), t);
                moveSinglePlayer(player, t);

                boolean resetCapacity = false;
                updateEdges(resetCapacity); //reset capacity and calculates new weight
            }

            if(flowPaths.isEmpty()) {
                maxFlowIncrementValue = maxFlow;
            } else {
                maxFlowIncrementValue += getIncrementValue(flowPaths, flowPathsReached, t, simpleDebug);
            }
            maxFlowOverTime += maxFlowIncrementValue;

            if(allPlayersFinished()) {
                break;
            }
        }

        double maxFlowWeightedAverage = maxFlowOverTime / t;
        selfishFlow = getSelfishFlow(t);
        if(simpleDebug) {
            System.out.println("Number of Players Done: " + playersDone.size());
            System.out.println("Number of Epochs Used: " + t);
            System.out.println("Max Flow: " + maxFlow);
            System.out.println("Max Flow Over Time: " + maxFlowOverTime);
            System.out.println("Max Flow Average: " + maxFlowWeightedAverage);
            System.out.println("Selfish Flow: " + selfishFlow);
        }
        double PoA = maxFlowWeightedAverage/ selfishFlow;
        return PoA;
    }

    public void createPlayers(int numberOfPlayers) {
        for(int i=0; i<numberOfPlayers; i++) {
            Player temp = new Player();
            players.add(temp);
            playersBacklog.add(temp);
        }
    }

    public ArrayList<FlowEdge> getShortestPath(String Start, String End) {
        DijkstraShortestPath<String, FlowEdge> dijkstra = new DijkstraShortestPath<>(graph);
        List<FlowEdge> shortestPathList = dijkstra.getPath(Start, End).getEdgeList();

        return new ArrayList<>(shortestPathList);
    }

    public void addPlayers(ArrayList<FlowEdge> shortestPath, double maxFlow, int t, double chanceOfPlayerEntering) {
        for(int i=0; i<maxFlow; i++) {
            if(i >= playersBacklog.size()) {
                break;
            }
            Player player = playersBacklog.get(i);
            addSinglePlayer(player, (ArrayList<FlowEdge>) shortestPath.clone(), t);
        }
        playersBacklog.removeAll(playersToRemove);
        playersToRemove.clear();

        for(Player player : playersBacklog) {
            double rand = Math.random();
            if(rand < chanceOfPlayerEntering) {
                addSinglePlayer(player, (ArrayList<FlowEdge>) shortestPath.clone(), t);
            }
        }
        playersBacklog.removeAll(playersToRemove);
        playersToRemove.clear();
    }

    public void addSinglePlayer(Player player, ArrayList<FlowEdge> path, int t) {
        playersInGame.add(player);
        player.setTimeStarted(t);

        player.setPath(path);
        handleVariablesForMovement(player, player.getEdge());
    }

    public void movePlayers(int t) {
        for(Player player : playersInGame) {
            moveSinglePlayer(player, t);
        }

        playersInGame.removeAll(playersToRemove);
        playersDone.addAll(playersToRemove);
        playersToRemove.clear();
    }

    public void moveSinglePlayer(Player player, int t) {
        if(player.isInsideEdge()) {
            player.moveForward();
            if(player.finishedEdge()) {
                movePlayerToNextEdge(player, t);
            }
        } else {
            handleVariablesForMovement(player, player.getEdge());
        }

    }

    public void movePlayerToNextEdge(Player player, int t) {
        FlowEdge oldEdge = player.removeOldEdge();
        oldEdge.getPlayersInEdge().remove(player);

        if(player.reachedEndOfGraph()) {
            handlePlayerReachingEnd(player, t);
            return;
        }

        FlowEdge nextEdge = player.getEdge();
        handleVariablesForMovement(player, nextEdge);
    }

    public void handlePlayerReachingEnd(Player player, int t) {
        playersToRemove.add(player);
        player.calculateTimeTotal(t);
    }

    public void handleVariablesForMovement(Player player, FlowEdge nextEdge) {
        player.setPosition(nextEdge);

        if(!player.isInQueue()) {
            nextEdge.getQueue().add(player); //if no space is available, the player waits in a queue
            player.setRemainingTimeForEdge(-1);
        }

        //checks if there's any room available for the player to join the edge
        if (nextEdge.isOpen() && player.canLeaveQueue()) {
            nextEdge.getPlayersInEdge().add(player);
            nextEdge.getQueue().remove(player);
            nextEdge.decrementCapacity();
            player.setRemainingTimeForEdge(nextEdge.getActualWeight());
        }
    }

    public void updateEdges(boolean resetCapacity) {
        for(FlowEdge edge : graph.edgeSet()) {
            if(resetCapacity) {
                edge.setRemainingCapacity(edge.getCapacity());
            }
            double newWeight = edge.calculateAggregateWeight();
            graph.setEdgeWeight(edge, newWeight);
        }
    }

    private boolean allPlayersFinished() {
        return playersDone.size() == players.size();
    }

    private double getSelfishFlow(int t) {
        return (double) playersDone.size() / t;
    }

    public double getMaxFlow(Graph<String, FlowEdge> graph) {
        EdmondsKarpMFImpl<String, FlowEdge> ek = new EdmondsKarpMFImpl<>(graph);
        return ek.calculateMaximumFlow("Start", "End");
    }

    public double getIncrementValue(List<GraphPath<String, FlowEdge>> flowPaths, List<GraphPath<String, FlowEdge>> flowPathsReached, int t, boolean simpleDebug) {
        // build graph with paths reached
        // get maxflow of that graph
        double incrementValue = 0;
        for(int i = 0; i < flowPaths.size(); i++) {
            GraphPath<String, FlowEdge> path = flowPaths.get(i);
            if(t >= path.getWeight()) {
                flowPathsReached.add(path);
                flowPaths.remove(path);
            }

            if(!flowPathsReached.isEmpty()) {
                CustomGraph tempBuilder = new CustomGraph(flowPathsReached);
                incrementValue = getMaxFlow(tempBuilder.getGraph());

                if(simpleDebug) {
                    System.out.println("Max Flow Increment: " + incrementValue);
                    System.out.println("--time: " + t);
                }
            }
        }

        return incrementValue;
    }
}