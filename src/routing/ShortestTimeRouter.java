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
        Map<State, PQEntry> best = new HashMap<>();
        Map<State, PreviousStep> previous = new HashMap<>();
        PriorityQueue<PQEntry> queue = new PriorityQueue<>();

        State startState = new State(start, null);
        PQEntry startEntry = new PQEntry(0.0, 0, startState);
        best.put(startState, startEntry);
        queue.add(startEntry);

        while (!queue.isEmpty()) {
            PQEntry entry = queue.poll();
            State current = entry.state;

            if (current.station.equals(end)) {
                return reconstruct(current, previous);
            }

            if (entry.compareTo(best.get(current)) > 0) {
                continue;
            }

            for (Edge edge : network.getEdges(current.station)) {
                boolean isChange = current.arrivedOn != null && !current.arrivedOn.equals(edge.getLine());
                double newTime = entry.time + edge.getTime() + (isChange ? CHANGE_PENALTY_MINS : 0);
                int newChanges = entry.changes + (isChange ? 1 : 0);

                State nextState = new State(edge.getDestination(), edge.getLine());
                PQEntry nextEntry = new PQEntry(newTime, newChanges, nextState);
                PQEntry currentBest = best.get(nextState);

                if (currentBest == null || nextEntry.compareTo(currentBest) < 0) {
                    best.put(nextState, nextEntry);
                    previous.put(nextState, new PreviousStep(current, edge));
                    queue.add(nextEntry);
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
        final int changes;

        /**
         * Create a PQEntry.
         * @param time the cumulative journey time to reach this state.
         * @param changes the cumulative number of line changes to reach this state.
         * @param state the state reached.
         */
        PQEntry(double time, int changes, State state) {
            this.time = time;
            this.changes = changes;
            this.state = state;
        }

        /**
         * Order by ascending journey time for use in a min-heap.
         * Secondarily uses number of changes as a tie breaker.
         */
        @Override
        public int compareTo(PQEntry other) {
            int byTime = Double.compare(this.time, other.time);
            if (byTime != 0) return byTime;
            return Integer.compare(this.changes, other.changes);
        }
    }
}
