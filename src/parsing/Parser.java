package parsing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

import model.*;


/**
 * Parses the Metrolink journey times CSV file and loads the data into a Network.
 */
public class Parser {

    /**
     * Reads the given CSV file and adds the stations and edges to the network.
     * The first line is skipped and blank lines are ignored.
     * @param path the path to the CSV file to read.
     * @param network the network object to be populated.
     */
    public static void load(Path path, Network network) throws IOException {
        List<String> lines = Files.readAllLines(path);
        String currentLineName = null;  

        // Iterate through each line (excluding the header), then split commas.
        for (String line : lines.subList(1, lines.size())) {

            if (line.isBlank()) {
                continue;
            }
            
            String[] fields = line.split(",");

            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].trim();
            }
 
            // If it's a line row, update the line variable.
            if (fields.length == 1) {
                currentLineName = fields[0];
            }

            // If it's an edge row, create an edge by calling addEdge.
            else if (fields.length == 3) {
                if (currentLineName == null) {
                    throw new IOException("Edge row before any line marker: " + line);
                }
                network.addEdge(fields[0], fields[1], currentLineName, Double.parseDouble(fields[2]));
            }

            else {
                throw new IOException("Rows must have 1 or 3 columns");
            }
        }
    }
}