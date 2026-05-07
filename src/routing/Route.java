package routing;

import java.util.List;

import model.Edge;
import model.Line;
import routing.DijkstraRouter.Mode;

/**
 * Represents a route through the Metrolink network as a list of edges.
 */
public class Route {
    private final List<Edge> edges;
    private final Mode routeType;

    /**
     * Create a Route object from a list of edges.
     * @param edges the list of edges that make up the route.
     */
    public Route(List<Edge> edges, Mode routeType) {
        this.edges = edges;
        this.routeType = routeType;
    }

    /**
     * Return the list of edges that make up the route.
     * @return the list of edges.
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Return the total journey time in minutes, including a 2-minute penalty
     * for each line change.
     * @return the total journey time.
     */
    public double getTotalTime() {
        double total = 0;

        for (Edge edge : edges) {
            total += edge.getTime();
        }

        total += countChanges() * 2;
        return total;
    }

    /**
     * Return the number of line changes along the route.
     * @return the number of line changes.
     */
    public int countChanges() {
        int total = 0;
        Line previousLine = null;

        for (Edge edge : edges) {
            if (previousLine != null && !edge.getLine().equals(previousLine)) {
                total += 1;
            }

            previousLine = edge.getLine();
        }

        return total;
    }

    /**
     * Return the route represented as a String.
     */
    @Override
    public String toString() {
        Line previousLine = null;
        String string = "";
        
        if (routeType == Mode.FEWEST_CHANGES) {
            string = string + "*** Fewest Changes Route ***";
        }

        else {
            string = string + "*** Shortest Time Route ***";
        }

        string = string + "\n";

        for (Edge edge : edges) {
            if (previousLine != null && !edge.getLine().equals(previousLine)) {
                string = string + edge.getSource().getName() + " on " + previousLine.getName() + " line" + "\n";
                string = string + "**  Change Line to " + edge.getLine().getName() + " line ***" + "\n";
            }

            string = string + edge.getSource().getName() + " on " + edge.getLine().getName() + " line" + "\n";
            previousLine = edge.getLine();
        }

        string = string + edges.get(edges.size() - 1).getDestination().getName() + " on " + edges.get(edges.size() - 1).getLine().getName() + " line" + "\n";

        string = string + "\n" + "Overall Journey Time (mins) = " + Double.toString(getTotalTime());
        string = string + "\n" + "Number of Changes = " + Integer.toString(countChanges());
        
        return string;
    }
}