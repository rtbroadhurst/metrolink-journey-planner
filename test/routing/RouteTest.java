package routing;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import model.Edge;
import model.Line;
import model.Station;
import routing.DijkstraRouter.Mode;

/** Tests Route's accounting (change counting and total time with penalty). */
class RouteTest {

    private Edge edge(String from, String to, String line, double t) {
        return new Edge(new Station(from), new Station(to), new Line(line), t);
    }

    @Test
    void countsChangesBetweenLines() {
        Route r = new Route(List.of(
            edge("A", "B", "red", 5),
            edge("B", "C", "red", 5),
            edge("C", "D", "blue", 5)
        ), Mode.SHORTEST_TIME);
        assertEquals(1, r.countChanges());
    }

    @Test
    void totalTimeIncludesPenaltyPerChange() {
        Route r = new Route(List.of(
            edge("A", "B", "red", 5),
            edge("B", "C", "blue", 5)
        ), Mode.SHORTEST_TIME);
        // 10 minutes of travel + one 2-minute change penalty
        assertEquals(12.0, r.getTotalTime(), 1e-9);
    }

    @Test
    void singleLineRouteHasNoChanges() {
        Route r = new Route(List.of(
            edge("A", "B", "red", 2),
            edge("B", "C", "red", 3)
        ), Mode.SHORTEST_TIME);
        assertEquals(0, r.countChanges());
        assertEquals(5.0, r.getTotalTime(), 1e-9);
    }

    @Test
    void toStringLabelsRouteTypeAndSummary() {
        Route r = new Route(List.of(edge("A", "B", "red", 2)), Mode.FEWEST_CHANGES);
        String s = r.toString();
        assertTrue(s.contains("Fewest Changes Route"));
        assertTrue(s.contains("Number of Changes = 0"));
    }
}
