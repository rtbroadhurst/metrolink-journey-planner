package cli;

import java.util.Scanner;

import model.Network;
import model.Station;
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

        ShortestTimeRouter shortestTimeRouter = new ShortestTimeRouter(network);
        Route route = shortestTimeRouter.findRoute(start, end);

        if (route == null) {
            System.out.println("No route found");
            return;
        }

        System.out.println(route);
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

