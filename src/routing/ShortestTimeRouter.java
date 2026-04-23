package routing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import model.Edge;
import model.Line;
import model.Network;
import model.Station;

/**
 * Finds the shortest-time route between two stations using Dijkstra's algorithm.
 * A 2-minute penalty is applied at each line change.
 */
public class ShortestTimeRouter {
    private static final double CHANGE_PENALTY_MINS = 2.0;

    private final Network network;

    /**
     * Create a ShortestTimeRouter operating on the given network.
     * @param network the network to route over.
     */
    public ShortestTimeRouter(Network network) {
        this.network = network;
    }

    /**
     * Find the shortest-time route between two stations.
     * Returns null if no route exists.
     * @param start the starting station.
     * @param end the destination station.
     * @return the shortest-time Route, or null if unreachable.
     */
    public Route findRoute(Station start, Station end) {
        Map<State, Double> bestTime = new HashMap<>();
        Map<State, PreviousStep> previous = new HashMap<>();
        PriorityQueue<PQEntry> queue = new PriorityQueue<>();

        State startState = new State(start, null);
        bestTime.put(startState, 0.0);
        queue.add(new PQEntry(0.0, startState));

        while (!queue.isEmpty()) {
            PQEntry entry = queue.poll();
            State current = entry.state;
            double currentTime = entry.time;

            if (current.station.equals(end)) {
                return reconstruct(current, previous);
            }

            if (currentTime > bestTime.get(current)) {
                continue;
            }

            for (Edge edge : network.getEdges(current.station)) {
                double stepCost = edge.getTime();
                if (current.arrivedOn != null && !current.arrivedOn.equals(edge.getLine())) {
                    stepCost += CHANGE_PENALTY_MINS;
                }

                State nextState = new State(edge.getDestination(), edge.getLine());
                double newTime = currentTime + stepCost;

                if (newTime < bestTime.getOrDefault(nextState, Double.POSITIVE_INFINITY)) {
                    bestTime.put(nextState, newTime);
                    previous.put(nextState, new PreviousStep(current, edge));
                    queue.add(new PQEntry(newTime, nextState));
                }
            }
        }

        return null;
    }

    /**
     * Walk backwards from endState through the previous-step map to rebuild the route.
     */
    private Route reconstruct(State endState, Map<State, PreviousStep> previous) {
        LinkedList<Edge> edges = new LinkedList<>();
        State current = endState;
        while (previous.containsKey(current)) {
            PreviousStep step = previous.get(current);
            edges.addFirst(step.edge);
            current = step.previousState;
        }
        return new Route(edges);
    }

    private static final class PreviousStep {
        final State previousState;
        final Edge edge;

        /**
         * Create a PreviousStep.
         * @param previousState the state before this step.
         * @param edge the edge traversed to reach the current state.
         */
        PreviousStep(State previousState, Edge edge) {
            this.previousState = previousState;
            this.edge = edge;
        }
    }

    private static final class State {
        final Station station;
        final Line arrivedOn;

        /**
         * Create a State object.
         * @param station the station at this state.
         * @param arrivedOn the line arrived on, or null for the start state.
         */
        State(Station station, Line arrivedOn) {
            this.station = station;
            this.arrivedOn = arrivedOn;
        }

        /**
         * Two State objects are considered equal if station and arrivedOn are the same.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            State state = (State) o;
            return this.station.equals(state.station)
                && Objects.equals(this.arrivedOn, state.arrivedOn);
        }

        /**
         * Return a hash code based on station and arrivedOn.
         */
        @Override
        public int hashCode() {
            return Objects.hash(station, arrivedOn);
        }
    }

    private static final class PQEntry implements Comparable<PQEntry> {
        final double time;
        final State state;

        /**
         * Create a PQEntry.
         * @param time the cumulative journey time to reach this state.
         * @param state the state reached.
         */
        PQEntry(double time, State state) {
            this.time = time;
            this.state = state;
        }

        /**
         * Order by ascending journey time for use in a min-heap.
         */
        @Override
        public int compareTo(PQEntry other) {
            return Double.compare(this.time, other.time);
        }
    }
}
