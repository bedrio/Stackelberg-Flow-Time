package org.example;

import java.util.ArrayList;

public class Player {
    //TODO nextEdge in line
    //TODO calculate timepassed?

    ArrayList<FlowEdge> path = new ArrayList<>();
    FlowEdge position;
    int timeStarted = 0;
    int timeTotal = 0;
    double remainingTimeForEdge = -1;

    public Player() {
    }

    public FlowEdge getEdge() {
        return path.get(0);
    }

    public boolean isInQueue() {
        return this.position.getQueue().contains(this);
    }

    /**
     * determines whether a player is eligible to enter the queue, or if they are too far back in the queue
     */
    public boolean canLeaveQueue() {
        int queueIndex = this.position.getQueue().indexOf(this);
        if(queueIndex == -1) {
            return true; //situation where there is no queue
        }

        double remainingCapacity = this.position.getRemainingCapacity();
        return (queueIndex + 1) <= remainingCapacity; // +1 to account for arrays starting at index 0
    }

    public boolean isInsideEdge() {
        return this.position.getPlayersInEdge().contains(this);
    }

    public void moveForward() {
        this.remainingTimeForEdge = this.remainingTimeForEdge - 1;
    }

    public boolean finishedEdge() {
        return this.remainingTimeForEdge == 0;
    }

    public FlowEdge removeOldEdge() {
        return this.path.remove(0);
    }

    public boolean reachedEndOfGraph() {
        return this.path.isEmpty();
    }

    public void calculateTimeTotal(int t) {
        this.timeTotal = t - this.timeStarted;
    }

    public ArrayList<FlowEdge> getPath() {
        return path;
    }
    public void setPath(ArrayList<FlowEdge> path) {
        this.path = path;
    }

    public FlowEdge getPosition() {
        return position;
    }
    public void setPosition(FlowEdge position) {
        this.position = position;
    }

    public int getTimeStarted() {
        return timeStarted;
    }
    public void setTimeStarted(int timeStarted) {
        this.timeStarted = timeStarted;
    }

    public int getTimeTotal() {
        return timeTotal;
    }
    public void setTimeTotal(int timeTotal) {
        this.timeTotal = timeTotal;
    }

    public double getRemainingTimeForEdge() {
        return remainingTimeForEdge;
    }
    public void setRemainingTimeForEdge(double remainingTimeForEdge) {
        this.remainingTimeForEdge = remainingTimeForEdge;
    }
}
