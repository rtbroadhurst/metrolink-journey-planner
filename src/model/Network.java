package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Metrolink network as a graph of stations connected by edges,
 * where each edge belongs to a specific line. The graph uses an adjacency
 * list that maps each station to the list of edges leaving it.
 */
public class Network {
    private Map<String, Station> stations = new HashMap<>();
    private Map<String, Line> lines = new HashMap<>();
    private Map<Station, List<Edge>> adjacency = new HashMap<>();
    
    /**
     * Create an empty network.
     */
    public Network() {
    }

    /**
     * Add a two way connection between two stations on a given line.
     * Creates the stations and line if they do not already exist.
     * @param from the name of the starting station.
     * @param to the name of the destination station.
     * @param lineName the name of the line the connection is on.
     * @param time the travel time between the stations, in minutes.
     */
    public void addEdge(String from, String to, String lineName, double time){
        Station fromStation = addStation(from);
        Station toStation = addStation(to);
        Line line = addLine(lineName);

        adjacency.get(fromStation).add(new Edge(fromStation, toStation, line, time));
        adjacency.get(toStation).add(new Edge(toStation, fromStation, line, time));
    }

    /**
     * Return the list of edges leaving a given station.
     * @param station the station to look up.
     * @return the list of edges leaving the station, or null if the station is not in the network.
     */
    public List<Edge> getEdges(Station station) {
        return adjacency.get(station);
    }
    
    /**
     * Add a station with the given name to the network, if it does not already exist.
     * @param name the name of the station.
     * @return the existing or newly created Station object.
     */
    private Station addStation(String name) {
        if (!stations.containsKey(name)) {
            Station s = new Station(name);
            stations.put(name, s);
            adjacency.put(s, new ArrayList<>()); 
        }
        return stations.get(name);
    }

    /**
     * Add a line with the given name to the network, if it does not already exist.
     * @param name the name of the line.
     * @return the existing or newly created Line object.
     */
    private Line addLine(String name) {
        if (!lines.containsKey(name)) {
            Line l = new Line(name);
            lines.put(name, l);
        }
        return lines.get(name);
    }
}