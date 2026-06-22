package routing;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import model.Network;
import model.Station;
import routing.DijkstraRouter.Mode;

/** Behavioural tests for the routing algorithm. */
class DijkstraRouterTest {

    private Route route(Network n, String from, String to, Mode mode) {
        return new DijkstraRouter(n, mode).findRoute(n.getStation(from), n.getStation(to));
    }

    @Test
    void unreachableDestinationReturnsNull() {
        Network n = new Network();
        n.addEdge("A", "B", "red", 1);
        n.addEdge("C", "D", "red", 1); // disconnected component
        assertNull(route(n, "A", "C", Mode.SHORTEST_TIME));
    }

    @Test
    void startEqualsEndGivesEmptyRouteNotNull() {
        Network n = new Network();
        n.addEdge("A", "B", "red", 1);
        Route r = route(n, "A", "A", Mode.SHORTEST_TIME);
        assertNotNull(r);
        assertTrue(r.getEdges().isEmpty());
        assertEquals(0.0, r.getTotalTime(), 1e-9);
    }

    @Test
    void lineChangeAddsTwoMinutePenalty() {
        Network n = new Network();
        n.addEdge("A", "B", "red", 5);
        n.addEdge("B", "C", "blue", 5);
        Route r = route(n, "A", "C", Mode.SHORTEST_TIME);
        assertEquals(1, r.countChanges());
        assertEquals(12.0, r.getTotalTime(), 1e-9); // 5 + 5 + 2 penalty
    }

    @Test
    void modeChangesTheChosenRoute() {
        Network n = new Network();
        n.addEdge("A", "B", "red", 10);
        n.addEdge("B", "C", "red", 10);
        n.addEdge("C", "D", "red", 10);  // path 1: 0 changes, 30 mins
        n.addEdge("A", "E", "green", 1);
        n.addEdge("E", "D", "blue", 1);   // path 2: 1 change, 4 mins
        Station a = n.getStation("A"), d = n.getStation("D");

        Route fastest = new DijkstraRouter(n, Mode.SHORTEST_TIME).findRoute(a, d);
        assertEquals(4.0, fastest.getTotalTime(), 1e-9);
        assertEquals(1, fastest.countChanges());

        Route fewest = new DijkstraRouter(n, Mode.FEWEST_CHANGES).findRoute(a, d);
        assertEquals(0, fewest.countChanges());
        assertEquals(30.0, fewest.getTotalTime(), 1e-9);
    }

    @Test
    void picksFasterOfTwoParallelLines() {
        // Real data: Bury->Whitefield exists on both yellow (9.0) and green (8.5)
        Network real = realNetwork();
        Route r = route(real, "Bury", "Whitefield", Mode.SHORTEST_TIME);
        assertEquals(8.5, r.getTotalTime(), 1e-9);
        assertEquals(0, r.countChanges());
    }

    @Test
    void routeIsSymmetric() {
        Network real = realNetwork();
        double there = route(real, "Bury", "Whitefield", Mode.SHORTEST_TIME).getTotalTime();
        double back  = route(real, "Whitefield", "Bury", Mode.SHORTEST_TIME).getTotalTime();
        assertEquals(there, back, 1e-9);
    }

    @Test
    void adjacentStationsGiveSingleEdge() {
        Network real = realNetwork();
        Route r = route(real, "East Didsbury", "Didsbury Village", Mode.SHORTEST_TIME);
        assertEquals(1, r.getEdges().size());
        assertEquals(1.0, r.getTotalTime(), 1e-9);
    }

    private Network realNetwork() {
        Network n = new Network();
        try {
            parsing.Parser.load(Paths.get("data/Metrolink_times_linecolour.csv"), n);
        } catch (Exception e) {
            throw new RuntimeException("Could not load real CSV (run from repo root)", e);
        }
        return n;
    }
}
