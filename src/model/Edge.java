package model;

public class Edge {
    private final Station source;
    private final Station destination;
    private final Line line;
    private final double time;

    
    public Edge(Station source, Station destination, Line line, double time) {
        this.source = source;
        this.destination = destination;
        this.line = line;
        this.time = time;
    }
    
    public Station getSource() {
        return this.source;
    }

    public Station getDestination() {
        return this.destination;
    }

    public Line getLine() {
        return this.line;
    }

    public double getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return source + " -> " + destination + " [" + line + ", " + time + " mins]";
    }
}
