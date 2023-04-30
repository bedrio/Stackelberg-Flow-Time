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

        double maxFlow = getMaxFlow();
        double selfishFlow = 0;
        for(int t = 0; t < numberOfEpochs; t++) {
            ArrayList<FlowEdge> shortestPath = getShortestPath("Start", "End"); //all players that enter this epoch will have the same path
            if(!playersBacklog.isEmpty()) {
                addPlayers(shortestPath, maxFlow, t, chanceOfPlayerEntering);
            }

            movePlayers(t);
            updateEdges(); //reset capacity and calculates new weight

            //if all players reached the end, then stop simulation
            if(allPlayersFinished()) {
                selfishFlow = getSelfishFlow(t);

                if(simpleDebug) {
                    System.out.println("Number of Players Done: " + playersDone.size());
                    System.out.println("Number of Epochs Used: " + t);
                    System.out.println("Max Flow: " + maxFlow);
                    System.out.println("Selfish Flow: " + selfishFlow);
                }
                break;
            }
        }

        return selfishFlow / maxFlow;
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
        return (double) players.size() / t;
    }

    public double getMaxFlow() {
        Graph<String, FlowEdge> capacityGraph = builder.getCapacityGraph();

        EdmondsKarpMFImpl<String, FlowEdge> ek = new EdmondsKarpMFImpl<>(capacityGraph);
        return ek.calculateMaximumFlow("Start", "End");
    }

}