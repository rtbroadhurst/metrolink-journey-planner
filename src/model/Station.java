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

    /**
     * Two Station objects are considered equal if they have the same name.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;
        return name.equals(station.name);
    }

    /**
     * Return a hash code based on the station name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}