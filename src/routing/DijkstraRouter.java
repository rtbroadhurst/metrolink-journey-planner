package routing;

import java.util.Comparator;
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
 * Finds a route between two stations using Dijkstra's algorithm.
 * Optimises for either shortest time or fewest changes.
 */
public class DijkstraRouter {

    static final double CHANGE_PENALTY_MINS = 2.0;

    public enum Mode {
        SHORTEST_TIME,
        FEWEST_CHANGES
    }

    private final Network network;
    private final Mode mode;

    private static final Comparator<PQEntry> BY_TIME =
        (a, b) -> {
            int byTime = Double.compare(a.time, b.time);
            if (byTime != 0) return byTime;
            return Integer.compare(a.changes, b.changes);
        };

    private static final Comparator<PQEntry> BY_CHANGES =
        (a, b) -> {
            int byChanges = Integer.compare(a.changes, b.changes);
            if (byChanges != 0) return byChanges;
            return Double.compare(a.time, b.time);
        };

    /**
     * Create a DijkstraRouter operating on a given network, and with a given mode.
     * @param network the network to route over.
     * @param mode the mode of routing: SHORTEST_TIME, or FEWEST_CHANGES.
     */
    public DijkstraRouter(Network network, Mode mode) {
        this.network = network;
        this.mode = mode;
    }

    /**
     * Find a route between two stations.
     * Search for either the shortest-time, or the fewest-changes depending on the mode.
     * Returns null if no route exists.
     * @param start the starting station.
     * @param end the destination station.
     * @return the route or null if unreachable.
     */
    public Route findRoute(Station start, Station end) {
        Map<State, PQEntry> best = new HashMap<>();
        Map<State, PreviousStep> previous = new HashMap<>();
        Comparator<PQEntry> comparator = (mode == Mode.FEWEST_CHANGES) ? BY_CHANGES : BY_TIME;
        PriorityQueue<PQEntry> queue = new PriorityQueue<>(comparator);

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

            if (comparator.compare(entry, best.get(current)) > 0) {
                continue;
            }

            for (Edge edge : network.getEdges(current.station)) {
                boolean isChange = current.arrivedOn != null && !current.arrivedOn.equals(edge.getLine());
                double newTime = entry.time + edge.getTime() + (isChange ? CHANGE_PENALTY_MINS : 0);
                int newChanges = entry.changes + (isChange ? 1 : 0);

                State nextState = new State(edge.getDestination(), edge.getLine());
                PQEntry nextEntry = new PQEntry(newTime, newChanges, nextState);
                PQEntry currentBest = best.get(nextState);

                if (currentBest == null || comparator.compare(nextEntry, currentBest) < 0) {
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
        return new Route(edges, mode);
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

    private static final class PQEntry {
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
    }   
}


