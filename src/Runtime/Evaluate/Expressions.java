package Runtime.Evaluate;

import Ast.Statements.*;
import Ast.Types.Enums.NodeType;
import Ast.Types.Statement;
import Constants.ReservedKeys;
import Exceptions.AlreadyDeclaredVariableException;
import Exceptions.InvalidCallException;
import Exceptions.InvalidNodeException;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;
import Runtime.Values.*;
import Types.Pair;

import java.util.ArrayList;

public class Expressions
{
    public static float evaluateDivision(Number left, Number right)
    {
        //TODO: checks, like zero division
        return left.floatValue() / right.floatValue();
    }

    public static RuntimeValue evaluateIdentifier(Identifier identifier, Environment env)
    {
        return env.lookupVariable(identifier.get());
    }

    public static RuntimeValue evaluateNumericBinaryExpr(NumberValue left, NumberValue right, String operator)
    {
        float result = 0.0F;
        if (operator.length() == 1)
        {
            result = switch (operator.toCharArray()[0])
            {
                case ReservedKeys.Division -> evaluateDivision(left.number, right.number);
                case ReservedKeys.Multiplication -> left.number.floatValue() * right.number.floatValue();
                case ReservedKeys.Plus -> left.number.floatValue() + right.number.floatValue();
                case ReservedKeys.Minus -> left.number.floatValue() - right.number.floatValue();
                default -> left.number.floatValue() % right.number.floatValue();
            };
        }
        return NumberValue.create(result);
    }

    public static RuntimeValue evaluateBinaryExpr(BinaryExpr expr, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue leftHandSide = Interpreter.evaluate(expr.left, env);
        RuntimeValue rightHandSide = Interpreter.evaluate(expr.right, env);

        if (leftHandSide.type == ValueType.Number
                && rightHandSide.type == ValueType.Number)
        {
            return evaluateNumericBinaryExpr(
                    (NumberValue) leftHandSide,
                    (NumberValue) rightHandSide,
                    expr.operator);
        }

        return NullValue.create();
    }

    public static RuntimeValue evaluateVariableAssignment(
            AssignmentExpr assignment, Environment env) throws AlreadyDeclaredVariableException
    {
        if (assignment.type != NodeType.AssignmentExpression)
        {
            throw new InvalidNodeException("An Identifier is expected to assign a variable.");
        }

        String name = ((Identifier) assignment.assigned).get();
        RuntimeValue value = Interpreter.evaluate(assignment.value, env);
        return env.assignVariable(name, value);
    }

    public static RuntimeValue evaluateObjectExpression(
            ObjectLiteral object, Environment env) throws AlreadyDeclaredVariableException
    {
        ObjectValue value = ObjectValue.create();

        for (Property prop : object.properties)
        {
            if (prop.value != null)
            {
                value.properties.put(prop.key, Interpreter.evaluate(prop.value, env));
                continue;
            }

            value.properties.put(prop.key, env.lookupVariable(prop.key));
        }

        return value;
    }

    public static RuntimeValue evaluateMemberExpression(
            MemberExpr memberExpr, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue object = Interpreter.evaluate(memberExpr.object, env);

        if (object instanceof ObjectValue value)
        {
            /* TODO: this probably does not support computed properties, but we don't even have strings, so i will concern with this after */
            if (memberExpr.property instanceof Identifier id && value.properties.containsKey(id.get())) {
                return value.properties.get(id.get());
            }

            return NullValue.create();
        }

        // TODO: change this exception
        throw new InvalidNodeException("Incorrect object node");
    }

    public static RuntimeValue evaluateCallExpression(
            CallExpr call, Environment env) throws AlreadyDeclaredVariableException
    {
        ArrayList<RuntimeValue> args = new ArrayList<>();

        for (Expr expr : call.arguments) {
            args.add(Interpreter.evaluate(expr, env));
        }

        RuntimeValue caller = Interpreter.evaluate(call.caller, env);

        if (caller instanceof FunctionValue function) {
            Environment scope = Environment.create(function.declarationEnv);

            for (int i = 0; i < function.parameters.size(); i++)
            {
                String name = function.parameters.get(i);
                scope.declareVariable(name, args.get(i), false);
            }

            RuntimeValue result = NullValue.create();
            for (Statement statement : function.body)
            {
                result = Interpreter.evaluate(statement, scope);
            }

            return result;
        }

        if (caller instanceof NativeFunctionValue) {
            return ((NativeFunctionValue) caller).call.apply(Pair.create(args, env));
        }

        throw new InvalidCallException("Cannot call a value that's not a NativeFunctionValue");
    }
}
