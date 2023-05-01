package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        boolean visualize = true;
        boolean simpleDebug = false;
        int numberOfPlayers = 500;
        int numberOfEpochs = 100000;
        int extraPlayers = 3;

        int graphNumber = 1;
        Simulation simulation = new Simulation("Vertices" + graphNumber + ".csv", "Edges" + graphNumber + ".csv");
        Simulation simulation_bounded = new Simulation("Vertices" + graphNumber + ".csv", "Edges" + graphNumber + "_improved.csv");

        double PoA = simulation.simulate(numberOfPlayers, numberOfEpochs, extraPlayers, visualize, simpleDebug);
        double PoA_bounded = simulation_bounded.simulate(numberOfPlayers, numberOfEpochs, extraPlayers, visualize, simpleDebug);
        System.out.println("Price of Anarchy: " + PoA);
        System.out.println("Bounded Price of Anarchy: " + PoA_bounded);
    }
}
