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
    public Player(ArrayList<FlowEdge> path) {
        this.setPath(path);
    }

    public FlowEdge getEdge() {
        return path.get(0);
    }

    public void iterateTime() {
        this.remainingTimeForEdge = this.remainingTimeForEdge - 1;
    }

    public void removeOldEdge() {
        this.path.remove(0);
    }

    public boolean reachedEnd() {
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
