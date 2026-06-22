package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/** Tests graph construction and lookups in Network. */
class NetworkTest {

    @Test
    void addEdgeCreatesBidirectionalConnection() {
        Network n = new Network();
        n.addEdge("A", "B", "red", 5.0);
        assertEquals(1, n.getEdges(n.getStation("A")).size());
        assertEquals(1, n.getEdges(n.getStation("B")).size());
    }

    @Test
    void getStationTrimsAndReturnsNullWhenAbsent() {
        Network n = new Network();
        n.addEdge("A", "B", "red", 5.0);
        assertNotNull(n.getStation("  A "));
        assertNull(n.getStation("ZZZ"));
    }

    @Test
    void stationsAreDeduplicatedAcrossEdges() {
        Network n = new Network();
        n.addEdge("A", "B", "red", 5.0);
        n.addEdge("B", "C", "red", 5.0);
        assertEquals(3, n.getAllStations().size());
        // B should now have two outgoing edges (to A and to C)
        assertEquals(2, n.getEdges(n.getStation("B")).size());
    }
}
