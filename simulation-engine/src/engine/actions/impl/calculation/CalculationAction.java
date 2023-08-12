package engine.actions.impl.calculation;

import engine.actions.api.AbstractAction;
import engine.actions.api.ActionType;
import engine.actions.expression.Expression;
import engine.actions.expression.ReturnType;
import engine.context.api.Context;
import engine.entity.impl.EntityDefinition;
import engine.properties.api.PropertyInterface;
import engine.properties.impl.DecimalProperty;
import engine.properties.impl.IntProperty;
import engine.value.generator.FixedValueGenerator;

// TODO: check if given property is indeed decimal/integer

enum CalculationType {
    MULTIPLY,
    DIVIDE
}

public class CalculationAction extends AbstractAction {

    private PropertyInterface resultProp;
    private Expression firstArgument;
    private Expression secondArgument;
    private String propertyName;
    private CalculationType calculationType;

    public CalculationAction(EntityDefinition entityDefinition, String propertyName, String calculationType, Expression firstArgument, Expression secondArgument) {
        super(ActionType.CALCULATION, entityDefinition);
        this.propertyName = propertyName;
        this.firstArgument = firstArgument;
        this.secondArgument = secondArgument;
        if (calculationType.equalsIgnoreCase("multiply")) {
            this.calculationType = CalculationType.MULTIPLY;
        }
        else if (calculationType.equalsIgnoreCase("divide")) {
            this.calculationType = CalculationType.DIVIDE;
        }
        else {
            // TODO: handle exception: not divide or multiply.
        }
    }
    @Override
    public void invoke(Context context) {
        resultProp = context.getPrimaryEntityInstance().getPropertyByName(propertyName);
        boolean isMultiply = (this.calculationType.equals(CalculationType.MULTIPLY));
        if (checkValidityOfExpressions()) {
            if (resultProp != null) {
                    switch (resultProp.getPropertyType()) {
                        case INT:
                            if(isMultiply) {
                                ((IntProperty) resultProp).setValue((int) firstArgument.getValue() * (int) secondArgument.getValue());
                            }
                            else {
                                if (!((int) secondArgument.getValue() == 0)) {
                                    ((IntProperty) resultProp).setValue((int) firstArgument.getValue() / (int) secondArgument.getValue());
                                }
                            }
                            break;
                        case DECIMAL:
                            if(isMultiply) {
                                ((DecimalProperty) resultProp).setValue((double) firstArgument.getValue() * (double) secondArgument.getValue());
                            }
                            else {
                                if (!((double) secondArgument.getValue() == 0)) {
                                    ((DecimalProperty) resultProp).setValue((double) firstArgument.getValue() / (double) secondArgument.getValue());
                                }
                            }
                            break;
                        default:
                            //TODO: handle error
                            break;

                }
            }
        }
    }

    private boolean checkValidityOfExpressions() {
        //TODO: is it possible to multiply integer by decimal? or all the 3 arguments should be from the same kind
        if (this.resultProp.getPropertyType().equals(ReturnType.DECIMAL)) {
            return ((!firstArgument.getReturnType().equals(ReturnType.INT) || !firstArgument.getReturnType().equals(ReturnType.DECIMAL)) ||
                    (!secondArgument.getReturnType().equals(ReturnType.INT) || !secondArgument.getReturnType().equals(ReturnType.DECIMAL)));
        } else {
            if (this.resultProp.getPropertyType().equals(ReturnType.INT)) {
                return firstArgument.getReturnType().equals(ReturnType.INT) && secondArgument.getReturnType().equals(ReturnType.INT);
            }
        }
        return false;
    }
}