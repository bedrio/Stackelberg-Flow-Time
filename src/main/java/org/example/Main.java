package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        boolean visualize = true;
        boolean simpleDebug = true;
        int numberOfPlayers = 100;
        int numberOfEpochs = 1000;
        double chanceOfPlayerEntering = 0.1; //After the max flow number of players enter, each individual player has this value's chance to enter as well

        Simulation simulation = new Simulation();

        double PoA = simulation.simulate(numberOfPlayers, numberOfEpochs, chanceOfPlayerEntering, visualize, simpleDebug);
        System.out.println("Price of Anarchy: " + PoA);
    }
}
