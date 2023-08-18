package engine.worldbuilder.factory;

import engine.Environment;
import engine.Rule;
import engine.Termination;
import engine.World;
import engine.entity.impl.EntityDefinition;
import engine.property.api.PropertyInterface;
import engine.worldbuilder.prdobjects.PRDAction;
import engine.worldbuilder.prdobjects.PRDEntity;
import engine.worldbuilder.prdobjects.PRDRule;
import engine.worldbuilder.prdobjects.PRDWorld;

import java.util.ArrayList;
import java.util.List;

public class WorldFactory {
     public static World BuildWorld(PRDWorld prdWorld) {
         List<PropertyInterface> envProperties = new ArrayList<>();
         Environment environment = EnvironmentFactory.BuildEnvironment(prdWorld.getPRDEvironment());

         List<EntityDefinition> entities = new ArrayList<>();
         for(PRDEntity entity : prdWorld.getPRDEntities().getPRDEntity()) {
             entities.add(EntityFactory.BuildEntity(entity));
         }
         // entities
         
         List<Rule> rules = new ArrayList<>();
         for(PRDRule rule : prdWorld.getPRDRules().getPRDRule()) {
             Rule currentRule = RuleFactory.BuildRule(rule);
             rules.add(currentRule);
         }
         // rules

         Termination termination = TerminationFactory.BuildTermination(prdWorld.getPRDTermination());
         // termination

         return new World(termination,entities,environment,rules);
     }
}
