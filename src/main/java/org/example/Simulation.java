package org.example;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Simulation {
    // Utility Variable. I cannot modify collection while looping through it, so this is a workaround
    private ArrayList<Player> playersToRemove = new ArrayList<>();
    private Graph<String, FlowEdge> graph;
    private CustomGraph builder;
    private ArrayList<Player> players;
    private ArrayList<Player> playersBacklog;
    private ArrayList<Player> playersInGame;
    private ArrayList<Player> playersDone;

    public Simulation() throws IOException {
        this.builder = new CustomGraph();
        this.graph = builder.createGraphFromFiles("Vertices.csv", "Edges.csv");

        //Very helpful for debugging
        this.players = new ArrayList<>(); //all players
        this.playersBacklog = new ArrayList<>(); //players that have not entered the game yet
        this.playersInGame = new ArrayList<>(); //players that are in the game
        this.playersDone = new ArrayList<>(); //players that finished the game
    }

    public double simulate(int numberOfPlayers, int numberOfEpochs, double chanceOfPlayerEntering, boolean visualize, boolean simpleDebug) throws IOException {
        if(visualize) {
            builder.visualize();
        }

        createPlayers(numberOfPlayers);

//        AllDirectedPaths<String, FlowEdge> allDirectedPaths = new AllDirectedPaths<>(graph);
//        List<GraphPath<String, FlowEdge>> flowPaths = allDirectedPaths.getAllPaths("Start", "End",false, 999999);
//        List<GraphPath<String, FlowEdge>> flowPathsReached = new ArrayList<>();
//        System.out.println(flowPaths);


        double maxFlow = getMaxFlow();
        double maxFlowOverTime = 0;
        double maxFlowIncrementValue = 0;
        double selfishFlow = 0;
        for(int t = 0; t < numberOfEpochs; t++) {
            ArrayList<FlowEdge> shortestPath = getShortestPath("Start", "End"); //all players that enter this epoch will have the same path
            if(!playersBacklog.isEmpty()) {
                addPlayers(shortestPath, maxFlow, t, chanceOfPlayerEntering);
            }


            movePlayers(t);
            updateEdges(); //reset capacity and calculates new weight

//            if(flowPaths.isEmpty()) {
//                maxFlowIncrementValue = maxFlow;
//            } else {
//                maxFlowIncrementValue += getIncrementValue(flowPaths, flowPathsReached, t);
//            }
//            maxFlowOverTime += maxFlowIncrementValue;

            selfishFlow = getSelfishFlow(t);
            if(simpleDebug) {
                System.out.println("Number of Players Done: " + playersDone.size());
                System.out.println("Number of Epochs Used: " + t);
                System.out.println("Max Flow: " + maxFlow);
                System.out.println("Selfish Flow: " + selfishFlow);
            }

            //if all players reached the end, then stop simulation
            if(allPlayersFinished()) {
                break;
            }
        }

//        double PoA = selfishFlow / (maxFlow / playersDone.size());
        return maxFlow / selfishFlow;
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
        playersToRemove.add(player);

        player.setPath(path);
        handleVariablesForMovement(player, player.getEdge());
    }

    public void movePlayers(int t) {
        for(Player player : playersInGame) {
            moveSinglePlayer(player, t);
        }

        playersInGame.removeAll(playersToRemove);
//        System.out.println(playersToRemove);
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

    public void updateEdges() {
        for(FlowEdge edge : graph.edgeSet()) {
            edge.setRemainingCapacity(edge.getCapacity());
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

    public double getMaxFlow() {
        Graph<String, FlowEdge> capacityGraph = builder.getCapacityGraph();

        EdmondsKarpMFImpl<String, FlowEdge> ek = new EdmondsKarpMFImpl<>(capacityGraph);
        return ek.calculateMaximumFlow("Start", "End");
    }

    public double getIncrementValue(List<GraphPath<String, FlowEdge>> flowPaths, List<GraphPath<String, FlowEdge>> flowPathsReached, int t) {
        double incrementValue = 0;
        for(int i = 0; i < flowPaths.size(); i++) {
            GraphPath<String, FlowEdge> path = flowPaths.get(i);

            if(t >= path.getWeight()) {
                if(flowPathsReached.isEmpty()) {
                    incrementValue += getMinCapacity((ArrayList<FlowEdge>) path.getEdgeList());
                } else {
                    incrementValue += getIntermediateStageIncrement(path, flowPathsReached);
                }

                flowPathsReached.add(path);
                flowPaths.remove(path);
            }
        }

        return incrementValue;
    }

    public double getMinCapacity(ArrayList<FlowEdge> path) {
        double minCapacity = Double.MAX_VALUE;
        for(FlowEdge edge : path) {
            if(edge.getCapacity() < minCapacity) {
                minCapacity = edge.getCapacity();
            }
        }

        return minCapacity;
    }

    private double getIntermediateStageIncrement(GraphPath<String, FlowEdge> newPath, List<GraphPath<String, FlowEdge>> flowPathsReached) {
        double lowestIncrementValue = Double.MAX_VALUE;
        for(GraphPath<String, FlowEdge> path : flowPathsReached) {
            double tempIncrement = getSharedEdgeIncrement(newPath, path);

            if(tempIncrement < lowestIncrementValue) {
                lowestIncrementValue = tempIncrement;
            }
        }

        return lowestIncrementValue;
    }

    private double getSharedEdgeIncrement(GraphPath<String, FlowEdge> newPath, GraphPath<String, FlowEdge> path) {
        double newIncrementValue = 0;

        if(pathsShareEdge(newPath, path)) {
            ArrayList<FlowEdge> sharedEdges = getSharedEdges(newPath, path);
            double oldMinCapacity = getMinCapacity((ArrayList<FlowEdge>) path.getEdgeList());
            double newMinCapacity = getMinCapacity((ArrayList<FlowEdge>) newPath.getEdgeList());
            double sharedMinCapacity = getMinCapacity(sharedEdges);
            if(sharedMinCapacity == oldMinCapacity) {
                newIncrementValue = 0;
            } else {
                double difference = sharedMinCapacity - oldMinCapacity;
                newIncrementValue = Math.min(difference, newMinCapacity);
            }
        }

        return newIncrementValue;
    }

    private boolean pathsShareEdge(GraphPath<String, FlowEdge> pathA, GraphPath<String, FlowEdge> pathB) {
        List<FlowEdge> edgeListA = pathA.getEdgeList();
        List<FlowEdge> edgeListB = pathB.getEdgeList();

        return edgeListA.retainAll(edgeListB); //retainAll returns a true/false boolean
    }

    //this assumes that the two paths do indeed share an edge
    private ArrayList<FlowEdge> getSharedEdges(GraphPath<String, FlowEdge> pathA, GraphPath<String, FlowEdge> pathB) {
        List<FlowEdge> edgeListA = pathA.getEdgeList();
        List<FlowEdge> edgeListB = pathB.getEdgeList();

        edgeListA.retainAll(edgeListB);
        return (ArrayList<FlowEdge>) edgeListA; //returns the retained edges as an ArrayList
    }

}