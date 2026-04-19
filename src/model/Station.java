package model;

/**
 * Represents a station on the Metrolink network.
 */
public class Station {
    private final String name;

    /**
     * Create a new Station with the given name.
     * Whitespace is trimmed.
     * @param name the name of the Station to be created
     */
    public Station(String name) {
        this.name = name.trim();
    }

    /**
     * Returns the name of the station.
     * @return the station's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Return a string representation of the station.
     */
    @Override
    public String toString() {
        return "Station(" + name + ")";
    }
}