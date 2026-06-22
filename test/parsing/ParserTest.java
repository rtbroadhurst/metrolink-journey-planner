package parsing;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import model.Network;

/** Tests CSV parsing: valid load, line-marker detection, and malformed-row handling. */
class ParserTest {

    private Path tempCsv(String content) throws IOException {
        Path p = Files.createTempFile("metrolink", ".csv");
        Files.writeString(p, content);
        p.toFile().deleteOnExit();
        return p;
    }

    @Test
    void parsesLineMarkersAndEdgeRows() throws IOException {
        // "red,," is a line marker (Java's split drops trailing empties -> length 1)
        String csv = "From,To,Time\nred,,\nA,B,5\nB,C,3\n";
        Network n = new Network();
        Parser.load(tempCsv(csv), n);
        assertEquals(3, n.getAllStations().size());
        assertNotNull(n.getStation("A"));
        assertEquals(1, n.getEdges(n.getStation("A")).size());
    }

    @Test
    void edgeRowBeforeAnyLineMarkerThrows() {
        String csv = "From,To,Time\nA,B,5\n";
        assertThrows(IOException.class, () -> Parser.load(tempCsv(csv), new Network()));
    }

    @Test
    void rowWithWrongColumnCountThrows() {
        String csv = "From,To,Time\nred,,\nA,B\n"; // 2-column edge row
        assertThrows(IOException.class, () -> Parser.load(tempCsv(csv), new Network()));
    }

    @Test
    void blankLinesAreIgnored() throws IOException {
        String csv = "From,To,Time\nred,,\n\nA,B,5\n\n";
        Network n = new Network();
        Parser.load(tempCsv(csv), n);
        assertEquals(2, n.getAllStations().size());
    }
}
