package model;

/**
 * Represents a connection between two stations on the network.
 * Each edge belongs to a specific line and stores a travel time.
 */
public class Edge {
    private final Station source;
    private final Station destination;
    private final Line line;
    private final double time;

    /**
     * Create an Edge object.
     * @param source the source station of the edge.
     * @param destination the destination station of the edge.
     * @param line the line that the edge belongs to.
     * @param time the time taken to travel the edge, in minutes. 
     */
    public Edge(Station source, Station destination, Line line, double time) {
        this.source = source;
        this.destination = destination;
        this.line = line;
        this.time = time;
    }
    
    /**
     * Return the source station of the edge.
     * @return the source station.
     */
    public Station getSource() {
        return this.source;
    }

    /**
     * Return the destination station of the edge. 
     * @return the destination station.
     */
    public Station getDestination() {
        return this.destination;
    }

    /**
     * Return the line that the edge is on.
     * @return the line that the edge is on.
     */
    public Line getLine() {
        return this.line;
    }

    /**
     * Return the travel time for the edge.
     * @return the travel time for the edge.
     */
    public double getTime() {
        return this.time;
    }

    /**
     * Return a string representation of the edge. 
     */
    @Override
    public String toString() {
        return source + " -> " + destination + " [" + line + ", " + time + " mins]";
    }
}