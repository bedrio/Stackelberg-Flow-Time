package org.example;

import java.io.IOException;


// have hardcoded variables for Arya to change
// effective max flow vs flow
// visualization add-ons
public class Main {
    public static void main(String[] args) throws IOException {
        boolean visualize = true;
        boolean simpleDebug = true;
        int numberOfPlayers = 1000;
        int numberOfEpochs = 1000;
        double chanceOfPlayerEntering = 0.1; //After the max flow number of players enter, each individual player has this value's chance to enter as well

        Simulation simulation = new Simulation("Vertices.csv", "Edges.csv");

        double PoA = simulation.simulate(numberOfPlayers, numberOfEpochs, chanceOfPlayerEntering, visualize, simpleDebug);
        System.out.println("Price of Anarchy: " + PoA);
    }
}
