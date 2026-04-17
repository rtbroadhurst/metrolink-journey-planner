public class Station {
    private final String name;

    public Station(String name) {
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Station(" + name + ")";
    }
}
