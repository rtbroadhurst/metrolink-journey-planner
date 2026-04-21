package routing;

import java.util.List;

import model.Edge;

public class Route {
    private final List<Edge> edges;

    public Route(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public double getTotalTime() {
        return 0; // Placeholder
    }

    @Override
    public String toString() {
        return ""; // Placeholder
    }
}
