package app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import model.Network;
import parsing.Parser;

/**
 * Entry point for the program.
 * Creates and populates the network then delegates to Cli.
 */
public class Main {
    public static void main(String[] args) {
        Path csv = Paths.get("data/Metrolink_times_linecolour.csv");
        Network network = new Network();

        try {
            Parser.load(csv, network);
            System.out.println("Network loaded successfully");
        } catch (IOException e) {
            System.err.println("Failed to load CSV: " + e.getMessage());
            System.exit(1);
        }


    }
}