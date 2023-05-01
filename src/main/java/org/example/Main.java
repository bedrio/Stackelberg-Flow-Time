package org.example;

import java.io.IOException;


// have hardcoded variables for Arya to change
// effective max flow vs flow
// visualization add-ons
public class Main {
    public static void main(String[] args) throws IOException {
        boolean visualize = false;
        boolean simpleDebug = false;
        int numberOfPlayers = 1000;
        int numberOfEpochs = 100000;
        double chanceOfPlayerEntering = 0.0; //After the max flow number of players enter, each individual player has this value's chance to enter as well
        int extraPlayers = 100;
//        double total_POA = 0;
//        double total_POA_improved = 0;
//        for (int i = 0; i < 100; i++) {
//            Simulation simulation = new Simulation("Vertices2.csv", "Edges2.csv");
//            Simulation simulation_improved = new Simulation("Vertices2.csv", "Edges2_improved.csv");
//
//            double PoA = simulation.simulate(numberOfPlayers, numberOfEpochs, chanceOfPlayerEntering, visualize, simpleDebug);
//            double PoA_improved = simulation_improved.simulate(numberOfPlayers, numberOfEpochs, chanceOfPlayerEntering, visualize, simpleDebug);
//
//            total_POA += PoA;
//            total_POA_improved += PoA_improved;
//        }
//
//        double avgPOA = total_POA/100;
//        double avgPOA_improved = total_POA_improved/100;
//        System.out.println("Price of Anarchy: " + avgPOA);
//        System.out.println("Price of Anarchy: " + avgPOA_improved);

        int graphNumber = 4;
        Simulation simulation = new Simulation("Vertices" + graphNumber + ".csv", "Edges" + graphNumber + ".csv");
        Simulation simulation_improved = new Simulation("Vertices" + graphNumber + ".csv", "Edges" + graphNumber + "_improved.csv");

        double PoA = simulation.simulate(numberOfPlayers, numberOfEpochs, extraPlayers, visualize, simpleDebug);
        double PoA_improved = simulation_improved.simulate(numberOfPlayers, numberOfEpochs, extraPlayers, visualize, simpleDebug);
        System.out.println("Price of Anarchy: " + PoA);
        System.out.println("Bounded Price of Anarchy: " + PoA_improved);
    }
}
