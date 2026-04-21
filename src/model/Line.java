package model;

/**
 * Represents a line on the Metrolink network.
 */
public class Line {
    private final String name;
    
    /**
     * Create a Line object with the given name.
     * Whitespace is trimmed.
     * @param name the name of the line to be created.
     */
    public Line(String name) {
        this.name = name.trim();
    }

    /**
     * Return the name of the line.
     * @return the name of the line.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return a string representation of the line.
     */
    @Override
    public String toString() {
        return "Line(" + name + ")";
    }

    /**
     * Two Line objects are considered equal if they have the same name.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;
        return name.equals(line.name);
    }

    /**
     * Return a hash code based on the line name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

}