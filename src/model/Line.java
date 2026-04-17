package model;

public class Line {
    private String name;
    
    public Line(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Line(" + name + ")";
    }
}

