package engine;

// TODO: make interface

enum TypeProperty {

}

public class Property {
    private String name;
    private int rangeFrom;
    private int rangeTo;
    private boolean isRandomlyGenerated;

    public Property(String name, int rangeFrom, int rangeTo, boolean isRandomlyGenerated) {
        this.name = name;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.isRandomlyGenerated = isRandomlyGenerated;
    }

    public String getName() { return name; }
}
