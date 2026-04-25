package app;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import cli.Cli;
import gui.Gui;
import model.Network;
import parsing.Parser;


/**
 * Entry point for the program.
 * Creates and populates the network then delegates to Gui, or Cli if the user inputs cli as an argument.
 */
public class Main {
    public static void main(String[] args) {
        Path csv = Paths.get("data/Metrolink_times_linecolour.csv");
        Network network = new Network();

        // Parse the csv.
        try {
            Parser.load(csv, network);

        } catch (IOException e) {
            System.err.println("Failed to load CSV: " + e.getMessage());
            System.exit(1);
        }

        // Runs the CLI if the user inputs 'cli' as an argument
        if (args.length > 0 && args[0].equals("cli")) {
            Cli cli = new Cli(network);
            cli.run();
        }  
        
        else {
            SwingUtilities.invokeLater(() -> new Gui(network));
        }
    }
}