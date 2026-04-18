package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {
    private Map<String, Station> stations = new HashMap<>();
    private Map<String, Line> lines = new HashMap<>();
    private Map<Station, List<Edge>> adjacency = new HashMap<>();
    
    public Network() {
    }

    public void addEdge(String from, String to, String lineName, double time){
        Station fromStation = addStation(from);
        Station toStation = addStation(to);
        Line line = addLine(lineName);

        adjacency.get(fromStation).add(new Edge(fromStation, toStation, line, time));
        adjacency.get(toStation).add(new Edge(toStation, fromStation, line, time));
    }

    public List<Edge> getEdges(Station station) {
        return adjacency.get(station);
    }
    
    private Station addStation(String name) {
        if (!stations.containsKey(name)) {
            Station s = new Station(name);
            stations.put(name, s);
            adjacency.put(s, new ArrayList<>()); 
        }
        return stations.get(name);
    }

    private Line addLine(String name) {
        if (!lines.containsKey(name)) {
            Line l = new Line(name);
            lines.put(name, l);
        }
        return lines.get(name);
    }
}