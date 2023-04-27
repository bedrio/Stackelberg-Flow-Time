package org.example;

import org.jgrapht.Graph;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;


/**
 * Change flow definition
 * number of players entering must be at least max flow
 * update aggregate weights
 * visualize
 * clean up the code
 *      Maybe handlePlayerMovement function can be the move edge function
 * file input to generate graph
 * have some pointers and comments to explain structure
 */


public class Main {
    public static void main(String[] args) throws InterruptedException {
        CustomGraph frame = new CustomGraph();
        frame.visualize();

        Graph<String, FlowEdge> graph = frame.getGraph();
        String Start = "Start";
        String End = "End";


        //create players
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Player> playersBacklog = new ArrayList<>();
        for(int i=0; i<20; i++) {
            Player temp = new Player();
            players.add(temp);
            playersBacklog.add(temp);
        }


        ArrayList<Player> playersInGame = new ArrayList<>();
        ArrayList<Player> playersToRemove = new ArrayList<>(); // I cannot modify collection while looping through it, so this is a workaround
        ArrayList<Player> playersDone = new ArrayList<>();
        for(int t = 0; t < 100; t++) {
            //shortest path remains constant in this single epoch
            DijkstraShortestPath<String, FlowEdge> dijkstra = new DijkstraShortestPath<>(graph);
            List<FlowEdge> shortestPathList = dijkstra.getPath(Start, End).getEdgeList();
            ArrayList<FlowEdge> shortestPath = new ArrayList<>(shortestPathList);
            // double shortestPathWeight = dijkstra.getPathWeight(Start, End);
            // System.out.println(shortestPath);
            // System.out.println("Shortest path weight: " + shortestPathWeight);

            //creates players and add them
            for(Player player : playersBacklog) {
                double rand = Math.random();
                if(rand < 0.3) {
                    playersInGame.add(player); //TODO put in function
                    addPlayerToGame(player, playersToRemove, (ArrayList<FlowEdge>) shortestPath.clone(), t);
                }
            }
            playersBacklog.removeAll(playersToRemove);
            playersToRemove.clear();


            for(Player player : playersInGame) {
                movePlayer(player, playersToRemove, t);
            }
           playersInGame.removeAll(playersToRemove);
           playersDone.addAll(playersToRemove);
           playersToRemove.clear();

            for(FlowEdge edge : graph.edgeSet()) {
                edge.setRemainingCapacity(edge.getCapacity());
                double newWeight = edge.calculateAggregateWeight();
                graph.setEdgeWeight(edge, newWeight);
            }

            //if all players reached the end, then stop simulation
            if(playersDone.size() == players.size()) {
                break;
            }
        }

        System.out.println(playersDone);
        System.out.println(playersDone.size());

        EdmondsKarpMFImpl<String, FlowEdge> ek = new EdmondsKarpMFImpl<>(graph);
        double maxFlow = ek.calculateMaximumFlow(Start, End);
        System.out.println(maxFlow);
    }

    public static void addPlayerToGame(Player player, ArrayList<Player> playersToRemove, ArrayList<FlowEdge> path, int t) {
        player.setTimeStarted(t);
        playersToRemove.add(player);

        player.setPath(path);
        handleVariablesForMovement(player, player.getEdge());
    }

    public static void movePlayer(Player player, ArrayList<Player> playersToRemove, int t) {
        if(player.isInsideEdge()) {
            player.moveForward();
            if(player.finishedEdge()) {
                movePlayerToNextEdge(player, playersToRemove, t);
            }
        } else {
            handleVariablesForMovement(player, player.getEdge());
        }

    }

    public static void movePlayerToNextEdge(Player player, ArrayList<Player> playersToRemove, int t) {
        FlowEdge oldEdge = player.removeOldEdge();
        oldEdge.getPlayersInEdge().remove(player);

        if(player.reachedEndOfGraph()) {
            handlePlayerReachingEnd(player, playersToRemove, t);
            return;
        }

        FlowEdge nextEdge = player.getEdge();
        handleVariablesForMovement(player, nextEdge);
    }

    public static void handlePlayerReachingEnd(Player player, ArrayList<Player> playersToRemove, int t) {
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
}