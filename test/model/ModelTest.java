package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/** Tests value semantics of Station and Line (trimming, equality, hashCode). */
class ModelTest {

    @Test
    void stationTrimsWhitespaceAndComparesByName() {
        Station a = new Station(" Bury ");
        Station b = new Station("Bury");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("Bury", a.getName());
    }

    @Test
    void differentStationsAreNotEqual() {
        assertNotEquals(new Station("Bury"), new Station("Radcliffe"));
    }

    @Test
    void stationNotEqualToNullOrOtherType() {
        Station s = new Station("Bury");
        assertNotEquals(s, null);
        assertNotEquals(s, "Bury");
    }

    @Test
    void lineTrimsWhitespaceAndComparesByName() {
        Line a = new Line("yellow");
        Line b = new Line(" yellow ");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void edgeExposesItsFields() {
        Station s = new Station("A");
        Station d = new Station("B");
        Line l = new Line("red");
        Edge e = new Edge(s, d, l, 5.0);
        assertEquals(s, e.getSource());
        assertEquals(d, e.getDestination());
        assertEquals(l, e.getLine());
        assertEquals(5.0, e.getTime(), 1e-9);
    }
}
