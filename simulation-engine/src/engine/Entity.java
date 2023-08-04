package engine;

import java.util.ArrayList;

public class Entity {
    private String name;
    private int population;
    private ArrayList<Property> propertyList;

    public Entity(String name, int population){
        this.name = name;
        this.population = population;
    }

    public void setProperties(ArrayList<Property> propertyList) {
        this.propertyList = propertyList;
    }
}
