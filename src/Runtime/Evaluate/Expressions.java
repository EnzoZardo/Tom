package Runtime.Evaluate;

import Ast.Expressions.*;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Ast.NodeType;
import Ast.Expressions.Literals.ObjectLiteral;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.*;
import Entities.Exceptions.InvalidCallException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Entities.Metadata.ParameterMetadata;
import Runtime.Environment;
import Runtime.Interpreter;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.*;
import Runtime.TypeChecker;
import Entities.Metadata.ArgumentMetadata;


import java.util.ArrayList;

public class Expressions
{
    //TODO: if something is not expected, throw exception, don't just return nullvalue
    public static float evaluateDivision(Number left, Number right)
    {
        ZeroDivisionException.ThrowIfZero(left);
        return left.floatValue() / right.floatValue();
    }

    public static RuntimeValue evaluateIdentifier(Identifier identifier, Environment env)
    {
        return env.lookupVariable(identifier.value);
    }

    public static NumericValue evaluateNumericAdditiveExpr(NumericValue left, NumericValue right, String operator)
    {
        float result = switch (operator)
        {
            case ReservedKeys.IntegerDivision -> (int) evaluateDivision(left.value, right.value);
            case ReservedKeys.Division -> evaluateDivision(left.value, right.value);
            case ReservedKeys.Multiplication -> left.value * right.value;
            case ReservedKeys.Minus -> left.value - right.value;
            case ReservedKeys.Plus -> left.value + right.value;
            case ReservedKeys.Mod -> left.value % right.value;
            default -> throw new InvalidOperatorException(operator);
        };

        boolean isFloat = !operator.equals(ReservedKeys.IntegerDivision);
        return NumericValue.create(result, left.isInteger && right.isInteger && !isFloat);
    }

    public static BooleanValue evaluateSizeOperator(RuntimeValue left, RuntimeValue right, String operator)
    {
        if (left.type != ValueType.Numeric || right.type != ValueType.Numeric)
        {
            throw new InvalidBinaryOperation(String.format("A operação %s só é permitida entre valores numéricos.",
                    operator));
        }

        NumericValue rightValue = (NumericValue) right;
        NumericValue leftValue = (NumericValue) left;

        boolean result = switch (operator)
        {
            case ReservedKeys.Minor -> rightValue.value < leftValue.value;
            case ReservedKeys.Greater -> rightValue.value > leftValue.value;
            case ReservedKeys.MinorOrEqual -> rightValue.value <= leftValue.value;
            case ReservedKeys.GreaterOrEqual -> rightValue.value >= leftValue.value;
            default -> throw new InvalidOperatorException(operator);
        };

        return BooleanValue.create(result);
    }

    public static BooleanValue evaluateBooleanBinaryExpr(RuntimeValue left, RuntimeValue right, String operator)
    {
        return switch (operator)
        {
            case ReservedKeys.Or -> BooleanValue.create(left.bool() || right.bool());
            case ReservedKeys.And -> BooleanValue.create(left.bool() && right.bool());
            case ReservedKeys.Minor,
                 ReservedKeys.Greater,
                 ReservedKeys.MinorOrEqual,
                 ReservedKeys.GreaterOrEqual -> evaluateSizeOperator(left, right, operator);
            case ReservedKeys.Equality -> BooleanValue.create(left.equals(right));
            case ReservedKeys.Difference -> BooleanValue.create(!left.equals(right));
            default -> throw new InvalidOperatorException(operator);
        };
    }

    public static StringValue evaluateStringAdditiveExpr(RuntimeValue left, RuntimeValue right, String operator)
    {
        if (ReservedKeys.Plus.equals(operator))
        {
            return StringValue.create(left.toString() + right.toString());
        };

        if (ReservedKeys.Multiplication.equals(operator))
        {
            final String message = "Não se pode multiplicar um texto por um valor não inteiro";
            if (left.type == ValueType.Numeric)
            {
                StringValue rightValue = (StringValue) right;
                NumericValue leftValue = (NumericValue) left;

                if (!leftValue.isInteger)
                {
                    throw new InvalidStringOperation(message);
                }

                return StringValue.create(rightValue.value.repeat((int) leftValue.value));
            }

            if (right.type == ValueType.Numeric)
            {
                NumericValue rightValue = (NumericValue) right;
                StringValue leftValue = (StringValue) left;

                if (!rightValue.isInteger)
                {
                    throw new InvalidStringOperation(message);
                }

                return StringValue.create(leftValue.value.repeat((int) rightValue.value));
            }
        }

        throw new InvalidStringOperation(String.format("Operação '%s' não permitida para valores do tipo texto.",
                operator));
    }

    public static RuntimeValue evaluateUnaryExpr(UnaryExpr expr, Environment env)
            throws AlreadyDeclaredVariableException
    {
        RuntimeValue rightHandSide = Interpreter.evaluate(expr.right, env);

        if (ReservedKeys.Not.equals(expr.operator))
        {
            return BooleanValue.create(rightHandSide.not());
        }

        if (ReservedKeys.Minus.equals(expr.operator) || ReservedKeys.Plus.equals(expr.operator)
            && rightHandSide.type == ValueType.Numeric)
        {
            NumericValue val = (NumericValue) rightHandSide;

            if (ReservedKeys.Minus.equals(expr.operator)) {
                return val.opposite();
            }

            return val;
        }

        throw new InvalidUnaryExpression();
    }

    public static RuntimeValue evaluateBinaryExpr(BinaryExpr expr, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue leftHandSide = Interpreter.evaluate(expr.left, env);
        RuntimeValue rightHandSide = Interpreter.evaluate(expr.right, env);

        if (ReservedOperators.isAdditiveOperator(expr.operator))
        {
            if (leftHandSide.type == ValueType.Numeric && rightHandSide.type == ValueType.Numeric)
            {
                return evaluateNumericAdditiveExpr(
                    (NumericValue) leftHandSide,
                    (NumericValue) rightHandSide,
                    expr.operator);
            }

            if (leftHandSide.type == ValueType.String || rightHandSide.type == ValueType.String)
            {
                return evaluateStringAdditiveExpr(leftHandSide, rightHandSide, expr.operator);
            }
        }

        if (ReservedOperators.isBooleanOperator(expr.operator))
        {
            return evaluateBooleanBinaryExpr(leftHandSide, rightHandSide, expr.operator);
        }

        throw new InvalidBinaryOperation();
    }

    public static RuntimeValue evaluateVariableAssignment(
            AssignmentExpr assignment, Environment env) throws AlreadyDeclaredVariableException
    {
        if (assignment.type != NodeType.AssignmentExpression)
        {
            throw new InvalidNodeException("An Identifier is expected to assign a variable.");
        }

        String name = ((Identifier) assignment.assigned).value;
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
            if (memberExpr.property instanceof Identifier id && value.properties.containsKey(id.value)) {
                return value.properties.get(id.value));
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

        if (caller.type == ValueType.NativeFunction) {
            return ((NativeFunctionValue) caller).call.apply(ParameterMetadata.create(args, env));
        }

        throw new InvalidCallException("Cannot call a value that's not a NativeFunctionValue");
    }
}
