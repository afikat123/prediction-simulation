package engine.worldbuilder.factory;

import engine.action.api.ActionInterface;
import engine.action.api.ActionType;
import engine.action.expression.Expression;
import engine.action.expression.ReturnType;
import engine.action.impl.calculation.CalculationAction;
import engine.action.impl.condition.impl.ConditionAction;
import engine.action.impl.condition.impl.MultipleConditionAction;
import engine.action.impl.increasedecrease.IncreaseDecreaseAction;
import engine.action.impl.kill.KillAction;
import engine.action.impl.set.SetAction;
import engine.entity.impl.EntityDefinition;
import engine.exception.*;
import engine.property.api.PropertyInterface;
import engine.worldbuilder.prdobjects.PRDAction;
import engine.worldbuilder.prdobjects.PRDCondition;
import engine.worldbuilder.prdobjects.PRDDivide;
import engine.xml.NewXMLReader;

import java.util.ArrayList;
import java.util.List;

public class ActionFactory {
    public static ActionInterface BuildAction(PRDAction prdAction, String ruleName) {
        ActionType actionType = ActionType.convert(prdAction.getType());
        ActionInterface resultAction = null;
        switch (actionType) {
            case INCREASE:
            case DECREASE:
                Expression expression = new Expression(prdAction.getBy());
                expression.FreeValuePositioning();
                resultAction = new IncreaseDecreaseAction(prdAction.getProperty(),
                        expression, prdAction.getType());
                CheckArgumentsTypeForNumbers(expression);
                SearchEntity(prdAction.getEntity(), ruleName);
                break;
            case CALCULATION:
                String calculationType = "";
                String arg1 = "", arg2 = "";
                PRDDivide prdDivide = prdAction.getPRDDivide();
                if (prdAction.getPRDDivide() == null) {
                    calculationType = "multiply";
                    arg1 = prdAction.getPRDMultiply().getArg1();
                    arg2 = prdAction.getPRDMultiply().getArg2();
                } else if (prdAction.getPRDMultiply() == null) {
                    calculationType = "divide";
                    arg1 = prdAction.getPRDDivide().getArg1();
                    arg2 = prdAction.getPRDDivide().getArg2();
                } else {
                    // TODO: throw exception
                }
                Expression arg1Expression = new Expression(arg1);
                Expression arg2Expression = new Expression(arg2);
                resultAction = new CalculationAction(prdAction.getResultProp(), calculationType, arg1Expression, arg2Expression);
                arg1Expression.FreeValuePositioning();
                arg2Expression.FreeValuePositioning();
                CheckArgumentsTypeForNumbers(arg1Expression);
                CheckArgumentsTypeForNumbers(arg2Expression);
                CheckIfResultPropExistAndInTheCorrectType(prdAction.getEntity(), prdAction.getResultProp(), ruleName);
                break;
            case CONDITION:
                List<ActionInterface> thenList = new ArrayList<>();
                List<ActionInterface> elseList = new ArrayList<>();
                List<PRDAction> prdThenList = prdAction.getPRDThen().getPRDAction();
                List<PRDAction> prdElseList = prdAction.getPRDElse() == null ? null : prdAction.getPRDElse().getPRDAction();
                for (PRDAction currentAction : prdThenList) {
                    thenList.add(ActionFactory.BuildAction(currentAction, ruleName));
                }
                if (prdElseList != null) {
                    for (PRDAction currentAction : prdElseList) {
                        elseList.add(ActionFactory.BuildAction(currentAction, ruleName));
                    }
                }

                if (prdAction.getPRDCondition().getSingularity().equalsIgnoreCase("single")) {
                    resultAction = new ConditionAction(prdAction.getPRDCondition().getProperty(), prdAction.getPRDCondition().getOperator(),
                            new Expression(prdAction.getPRDCondition().getValue()), thenList, elseList);
                    SearchEntityAndProperty(prdAction.getPRDCondition().getEntity(), prdAction.getPRDCondition().getProperty(), ruleName);
                } else if (prdAction.getPRDCondition().getSingularity().equalsIgnoreCase("multiple")) {
                    resultAction = new MultipleConditionAction(thenList, elseList, prdAction.getPRDCondition().getLogical(),
                            ConditionFactory.BuildConditionFromList(prdAction.getPRDCondition().getPRDCondition()));
                    CheckMultipleCondition(prdAction.getPRDCondition(), ruleName);
                }
                break;
            case SET:
                resultAction = new SetAction(prdAction.getProperty(), new Expression(prdAction.getValue()));
                break;
            case KILL:
                resultAction = new KillAction();
                break;
            case REPLACE:
                break;
            case PROXIMITY:
                break;
        }
        return resultAction;
    }

    public static boolean CheckIfEnvPropertyExistAndInTheCorrectType(Expression expression) {
        String envVariableName = "";
        String expressionValue = (String) expression.getValue();
        boolean found = false;
        int startIndex = expressionValue.indexOf("(");
        int endIndex = expressionValue.indexOf(")");
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            envVariableName = expressionValue.substring(startIndex + 1, endIndex);
        }
        for (PropertyInterface envProperty : NewXMLReader.envVariables) {
            if (envProperty.getName().equals(envVariableName)) {
                if (!(envProperty.getPropertyType().equals(ReturnType.INT) || envProperty.getPropertyType().equals(ReturnType.DECIMAL))) {
                    throw new XMLVariableTypeException("", expression.getReturnType(), envProperty.getPropertyType());
                } else {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new XMLEnvPropertyNotFound("", expressionValue);
        }
        return found;
    }

    public static void CheckArgumentsTypeForNumbers(Expression expression) {
        if (!(expression.getReturnType().equals(ReturnType.INT) || expression.getReturnType().equals(ReturnType.DECIMAL))) {
            String expressionValue = (String) expression.getValue();
            if (expressionValue.startsWith("environment(")) {
                CheckIfEnvPropertyExistAndInTheCorrectType(expression);
            } else if (expressionValue.startsWith("random(")) {
                ///TODO: roni
            } else if (expressionValue.startsWith("evaluate(")) {
                //TODO: handle
            } else if (expressionValue.startsWith("percent(")) {
                //TODO: handle
            } else if (expressionValue.startsWith("ticks(")) {
                //TODO: handle
            } else {
                throw new XMLVariableTypeException("", expression.getReturnType(), ReturnType.DECIMAL);
            }
        }
    }

    public static void CheckIfResultPropExistAndInTheCorrectType(String entityName, String entityProperty, String ruleName) {
        boolean found = false;
        for (EntityDefinition currentEntity : NewXMLReader.entityDefinitionList) {
            if (currentEntity.getName().equalsIgnoreCase(entityName)) {
                for (PropertyInterface currentProperty : currentEntity.getProps()) {
                    if (currentProperty.getName().equalsIgnoreCase(entityProperty)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new XMLEntityPropertyNotFound("", entityName, entityProperty, ruleName);
                }
            }
        }
        if (!found) {
            throw new XMLEntityNotFoundException("", ruleName, entityName);
        }

    }

    public static EntityDefinition SearchEntity(String entityName, String ruleName) {
        boolean found = false;
        EntityDefinition entityToReturn = null;
        for (EntityDefinition currentEntity : NewXMLReader.entityDefinitionList) {
            if (currentEntity.getName().equalsIgnoreCase(entityName)) {
                found = true;
                entityToReturn = currentEntity;
                break;
            }
        }
        if (!found) {
            throw new XMLEntityNotFoundException("", ruleName, entityName);
        } else {
            return entityToReturn;
        }

    }

    public static void SearchEntityAndProperty(String entityName, String propertyName, String ruleName) {
        EntityDefinition currentEntity = SearchEntity(entityName, ruleName);
        boolean found = false;
        for(PropertyInterface property : currentEntity.getProps()) {
            if(property.getName().equalsIgnoreCase(propertyName)) {
                found = true;
                break;
            }
        }
        if(!found) {
            throw new XMLEntityPropertyNotFound("", entityName, propertyName, ruleName);
        }
    }

    public static void CheckMultipleCondition(PRDCondition multipleCondition, String ruleName) {
        for(PRDCondition currentCondition : multipleCondition.getPRDCondition()) {
            if(currentCondition.getSingularity().equalsIgnoreCase("single")) {
                SearchEntityAndProperty(currentCondition.getEntity(), currentCondition.getProperty(), ruleName);
            } else {
                CheckMultipleCondition(currentCondition, ruleName);
            }
        }
    }
}
