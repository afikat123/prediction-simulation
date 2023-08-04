package enginewrapper;

import engine.*;

import java.util.ArrayList;

public class EngineWrapper {
    public static void main() {

        // initialize lists
        ArrayList<Entity> entities = new ArrayList<Entity>();
        ArrayList<Rule> rules = new ArrayList<Rule>();
        ArrayList<Property> properties = new ArrayList<Property>();

        // initialize entity
        Entity entity = new Entity("Gunslinger", 30);
        Property entityProperty1 = new IntProperty(21, "LifeLeft", 50, false);
        Property entityProperty2 = new DecimalProperty(3.14, "AimAmount", 60, false);
        properties.add(entityProperty1);
        properties.add(entityProperty2);
        entity.setProperties(properties);

        // initialize world
        Rule rule1 = new Rule("Aging", 1, 1);
        Rule rule2 = new Rule("PullingGun", 4, 0.75);
        Rule rule3 = new Rule("FindingAWoman", 6, 0.24);
        Action action1 = new Action();
        Action action2 = new Action();
        Action action3 = new Action();
        Action action4 = new Action();
        Engine engine = new Engine();
        World world = new World(300, );

        //

    }
}
