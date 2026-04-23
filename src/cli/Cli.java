package cli;

import java.util.Scanner;

import model.Network;
import model.Station;
import routing.FewestChangesRouter;
import routing.Route;
import routing.ShortestTimeRouter;

/**
 * Command Line Interface for the program.
 */
public class Cli {
    private final Network network;
    private final Scanner scanner = new Scanner(System.in);
    
    /**
     * Constructor for Cli.
     * @param network the network that the Cli is operating on.
     */
    public Cli(Network network) {
        this.network = network;
    }

    /**
     * Run the Cli.
     */
    public void run() {
        Station start = getStation("Enter start station: ");
        Station end = getStation("Enter end station: ");
        Route route = getRouter().findRoute(start, end);

        if (route == null) {
            System.out.println("No route found");
            return;
        }

        System.out.println(route);
    }

    private interface Router {
        Route findRoute(Station start, Station end);
    }

    private Router getRouter() {
        System.out.println("Choose routing option:");
        System.out.println("  1. Shortest time");
        System.out.println("  2. Fewest changes");
        while (true) {
            System.out.print("Enter 1 or 2: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": return new ShortestTimeRouter(network)::findRoute;
                case "2": return new FewestChangesRouter(network)::findRoute;
                default: System.out.println("Please enter 1 or 2.");
            }
        }
    }

    /**
     * Prompt the user to input a station name, repeating until valid.
     * @param message the prompt to display.
     * @return the validated Station.
     */
    private Station getStation(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            Station station = network.getStation(input);

            if (station != null) {
                return station;
            }

            System.out.println("Not a valid station.");
        }
    }
}

