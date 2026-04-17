package Runtime.Evaluate;

import Ast.Statements.Expressions.*;
import Ast.Enums.NodeType;
import Ast.Statements.Statement;
import Constants.ReservedKeys;
import Exceptions.AlreadyDeclaredVariableException;
import Exceptions.InvalidCallException;
import Exceptions.InvalidNodeException;
import Runtime.Environment;
import Runtime.Interpreter;
import Runtime.Types.Enums.ValueType;
import Runtime.Types.RuntimeValue;
import Runtime.Values.*;
import Runtime.TypeChecker;
import Types.ArgumentMetadata;
import Types.Pair;

import java.util.ArrayList;

public class Expressions
{
    //TODO: if something is not expected, throw exception, don't just return nullvalue
    public static float evaluateDivision(Number left, Number right)
    {
        //TODO: checks, like zero division
        return left.floatValue() / right.floatValue();
    }

    public static RuntimeValue evaluateIdentifier(Identifier identifier, Environment env)
    {
        return env.lookupVariable(identifier.get());
    }

    public static RuntimeValue evaluateNumericBinaryExpr(NumericValue left, NumericValue right, String operator)
    {
        float result = switch (operator)
        {
            case ReservedKeys.Division -> evaluateDivision(left.number, right.number);
            case ReservedKeys.IntegerDivision -> (int) evaluateDivision(left.number, right.number);
            case ReservedKeys.Multiplication -> left.number * right.number;
            case ReservedKeys.Plus -> left.number + right.number;
            case ReservedKeys.Minus -> left.number - right.number;
            case ReservedKeys.Mod -> left.number % right.number;
            default -> 0;
        };

        boolean isFloat = !operator.equals(ReservedKeys.IntegerDivision);
        return NumericValue.create(result, left.isInteger && right.isInteger && !isFloat);
    }

    public static RuntimeValue evaluateUnaryExpr(UnaryExpr expr, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue rightHandSide = Interpreter.evaluate(expr.right, env);

        if (ReservedKeys.Not.equals(expr.operator))
        {
            return switch (rightHandSide.type) {
                case ValueType.Numeric -> BooleanValue.create(((NumericValue) rightHandSide).number == 0);
                case ValueType.Boolean -> BooleanValue.create(!((BooleanValue) rightHandSide).value);
                case ValueType.String -> BooleanValue.create(((StringValue) rightHandSide).text.isEmpty());
                case ValueType.Object -> BooleanValue.create(
                    ((ObjectValue) rightHandSide).properties.isEmpty()
                    || ((ObjectValue) rightHandSide).properties.values().stream().allMatch(x -> x.type == ValueType.Null));
                case ValueType.Null -> BooleanValue.create(true);
                //TODO: throw
                default -> NullValue.create();
            };
        }

        if (ReservedKeys.Minus.equals(expr.operator)
            || ReservedKeys.Plus.equals(expr.operator)
            && rightHandSide.type == ValueType.Numeric)
        {
            NumericValue val = (NumericValue) rightHandSide;
            if (ReservedKeys.Minus.equals(expr.operator)) {
                return val.opposite();
            }
            return val;
        }

        //TODO: throw
        return NullValue.create();
    }

    public static RuntimeValue evaluateBinaryExpr(BinaryExpr expr, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue leftHandSide = Interpreter.evaluate(expr.left, env);
        RuntimeValue rightHandSide = Interpreter.evaluate(expr.right, env);

        if (leftHandSide.type == ValueType.Numeric && rightHandSide.type == ValueType.Numeric)
        {
            return evaluateNumericBinaryExpr(
                    (NumericValue) leftHandSide,
                    (NumericValue) rightHandSide,
                    expr.operator);
        }

        //TODO: throw
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

        //TODO: verify, maybe check types unless instanceof
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
                ArgumentMetadata param = function.parameters.get(i);
                String name = param.getName();

                if (!TypeChecker.check(env, args.get(i), param.getType())) {
                    throw new RuntimeException("Invalid Argument type");
                }

                scope.declareVariable(name, args.get(i), false);
            }

            RuntimeValue result = NullValue.create();
            for (Statement statement : function.body)
            {
                result = Interpreter.evaluate(statement, scope);
            }

            if (!TypeChecker.check(env, result, function.returnType)) {
                throw new RuntimeException("Invalid return type");
            }

            return result;
        }

        if (caller instanceof NativeFunctionValue) {
            return ((NativeFunctionValue) caller).call.apply(Pair.create(args, env));
        }

        throw new InvalidCallException("Cannot call a value that's not a NativeFunctionValue");
    }
}
