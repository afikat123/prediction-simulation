package engine.general.object;

import engine.action.api.ActionInterface;
import engine.context.api.Context;
import engine.context.impl.ContextImpl;
import engine.entity.impl.EntityInstance;
import engine.entity.impl.EntityInstanceManager;
import engine.grid.impl.Grid;

import java.util.List;
import java.util.Map;

public class Rule {
    private String name;
    RuleActivation activation;
    private List<ActionInterface> actions;

    public Rule(String name, List<ActionInterface> actions, RuleActivation activation) {
        this.name = name;
        this.activation = activation;
        this.actions = actions;
    }

    public void addAction(ActionInterface action) {
        actions.add(action);
    }

    public boolean activation(int currentTickCount) {
        return activation.checkActivation(currentTickCount);
    }

    public void invokeAction(Context context) {
        for (ActionInterface action : actions) {
            if (action.getEntityOfTheAction().equalsIgnoreCase(context.getPrimaryEntityName())) {
                action.invoke(context);
            }
        }
    }

    public void NewInvokeAction(Map<String, EntityInstanceManager> entityInstanceManager, Environment env, Grid grid) {
        for (Map.Entry<String, EntityInstanceManager> entry : entityInstanceManager.entrySet()) {
            for (EntityInstance entity : entry.getValue().getInstances()) {
                for (ActionInterface action : actions) {
                    if (action.getEntityOfTheAction().equalsIgnoreCase(entry.getValue().getEntityName())) {
                        Context context = new ContextImpl(entity, entityInstanceManager, env);
                        context.setGrid(grid);
                        if (!action.haveSecondaryEntity()) {
                            action.invoke(context);
                        } else {
                            List<EntityInstance> secondEntityList = entityInstanceManager.get(action.getSecondEntityName()).getInstances();
                            List <EntityInstance> secondaryChosen = action.getSecondaryEntityChooser().secondaryEntitiesListBuilder(context, secondEntityList);
                            for(EntityInstance secondaryEntity : secondaryChosen) {
                                context.addSecondEntity(secondaryEntity);
                                action.invoke(context);
                            }

                        }
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getTick() {
        return activation.getTicks();
    }

    public double getProbability() {
        return activation.getProbability();
    }

    public List<ActionInterface> getActions() {
        return actions;
    }

    public int GetNumOfActions() {
        return this.actions.size();
    }
}
