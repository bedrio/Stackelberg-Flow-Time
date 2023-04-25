package org.example;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;

/***
 * Fields include:
 *  Source
 *  Target
 *  Weight
 *  Capacity
 *  PlayersInEdge
 *  Queue
 */
public class FlowEdge extends DefaultWeightedEdge {
    double capacity = 0;
    double actualWeight = 0;
    ArrayList<Player> playersInEdge = new ArrayList<>();
    ArrayList<Player> queue = new ArrayList<>();

    public FlowEdge() {
    }

    public boolean isOpen() {
        return this.playersInEdge.size() < this.capacity;
    }

    //TODO Maybe implement dynamic calculation?
    // So far, this calculates the maximum delay possible
    public double calculateAggregateWeight() {
        return ( (queue.size() / this.capacity) * this.actualWeight ) + this.actualWeight;
    }

    public double getWeightSetInGraph() {
        return super.getWeight();
    }

    public double getCapacity() {
        return capacity;
    }
    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getActualWeight() {
        return actualWeight;
    }
    public void setActualWeight(double actualWeight) {
        this.actualWeight = actualWeight;
    }

    public ArrayList<Player> getPlayersInEdge() {
        return playersInEdge;
    }
    public void setPlayersInEdge(ArrayList<Player> playersInEdge) {
        this.playersInEdge = playersInEdge;
    }

    public ArrayList<Player> getQueue() {
        return queue;
    }
    public void setQueue(ArrayList<Player> queue) {
        this.queue = queue;
    }
}