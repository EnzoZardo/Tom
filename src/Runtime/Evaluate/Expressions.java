package Runtime.Evaluate;

import Ast.Expressions.*;
import Ast.Expressions.Literals.ArrayLiteral;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Ast.NodeType;
import Ast.Expressions.Literals.ObjectLiteral;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.*;
import Entities.Exceptions.ExpectedTypeNotMatch;
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
import java.util.Arrays;
import java.util.HashMap;

public class Expressions
{
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
        return NumericValue.create(result, left.isInteger && right.isInteger && isFloat);
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

    public static RuntimeValue evaluateBooleanBinaryExpr(RuntimeValue left, RuntimeValue right, String operator)
    {
        return switch (operator)
        {
            case ReservedKeys.Or -> BooleanValue.create(left.bool() || right.bool());
            case ReservedKeys.And -> BooleanValue.create(left.bool() && right.bool());
            case ReservedKeys.Equality -> BooleanValue.create(left.equals(right));
            case ReservedKeys.Difference -> BooleanValue.create(!left.equals(right));
            case ReservedKeys.Minor,
                 ReservedKeys.Greater,
                 ReservedKeys.MinorOrEqual,
                 ReservedKeys.GreaterOrEqual -> evaluateSizeOperator(left, right, operator);
            default -> throw new InvalidOperatorException(operator);
        };
    }

    public static RuntimeValue evaluateStringAdditiveExpr(RuntimeValue left, RuntimeValue right, String operator)
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

        if (ReservedKeys.Division.equals(operator) || ReservedKeys.IntegerDivision.equals(operator))
        {
            final String error = "Operação de divisão só é permitida entre texto e inteiro.";
            if (left.type != ValueType.String || right.type != ValueType.Numeric)
            {
                throw new InvalidStringOperation(error);
            }

            StringValue leftValue  = (StringValue) left;
            NumericValue rightValue = (NumericValue) right;

            if (!rightValue.isInteger)
            {
                throw new InvalidStringOperation(error);
            }

            int divisor = (int) rightValue.value;
            ZeroDivisionException.ThrowIfZero(divisor);

            String target = leftValue.value;

            if (divisor > target.length())
            {
                throw new InvalidStringOperation("Não se pode dividir um texto por um tamanho maior do que o seu.");
            }

            HashMap<Integer, RuntimeValue> items = new HashMap<>();
            int size = Math.ceilDiv(target.length(), divisor);
            int index = 0;

            for (int i = 0; i < target.length(); i += size) {
                String value = target.substring(i, Math.min(i + size, target.length()));
                items.put(index, StringValue.create(value));
                index++;
            }

            return ArrayValue.create(items);
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

        if (ReservedOperators.isNumericOperator(expr.operator))
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
            throw new InvalidNodeException("O nome da variável é esperado para atribuirmos ela.");
        }

        if (assignment.assigned.type == NodeType.Identifier)
        {
            String name = ((Identifier) assignment.assigned).value;
            RuntimeValue value = Interpreter.evaluate(assignment.value, env);
            return env.assignVariable(name, value);
        }

        if (assignment.assigned.type == NodeType.MemberExpression)
        {
            MemberExpr memberExpr = ((MemberExpr) assignment.assigned);
            RuntimeValue value = Interpreter.evaluate(assignment.value, env);

            //TODO: validate if object is of type object or array or anything else
            if (memberExpr.object.type == NodeType.Identifier)
            {
                Identifier objectIdentifier = (Identifier) memberExpr.object;

                if (!memberExpr.computed)
                {
                    Identifier memberIdentifier = (Identifier) memberExpr.property;
                    return env.assignMember(objectIdentifier.value, memberIdentifier.value, value);
                }

                RuntimeValue propValue = Interpreter.evaluate(memberExpr.property, env);

                if (propValue.type == ValueType.String) {
                    StringValue memberIdentifier = (StringValue) propValue;
                    return env.assignMember(objectIdentifier.value, memberIdentifier.value, value);
                }
            }

            if (memberExpr.object.type == NodeType.ObjectLiteral)
            {
                return NullValue.create();
            }
        }

        throw new InvalidNodeException("Esperávamos um membro de objeto ou uma variável para a expressão " +
            "darmos um novo valor para ela.");
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

    public static RuntimeValue evaluateArrayExpression(
            ArrayLiteral array, Environment env) throws AlreadyDeclaredVariableException
    {
        ArrayValue value = ArrayValue.create();

        for (int i = 0; i < array.items.size(); i++)
        {
            Expr item = array.items.get(i);
            RuntimeValue evaluated = Interpreter.evaluate(item, env);
            value.items.put(i, evaluated);
        }

        return value;
    }


    public static RuntimeValue evaluateMemberExpression(
            MemberExpr memberExpr, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue object = Interpreter.evaluate(memberExpr.object, env);

        if (object.type == ValueType.Object)
        {
            ObjectValue value = (ObjectValue) object;

            if (memberExpr.property.type == NodeType.Identifier && !memberExpr.computed)
            {
                Identifier id = (Identifier) memberExpr.property;
                if (value.properties.containsKey(id.value))
                {
                    return value.properties.get(id.value);
                }
            }
            else
            {
                RuntimeValue member = Interpreter.evaluate(memberExpr.property, env);

                if (member.type == ValueType.String)
                {
                    StringValue id = (StringValue) member;
                    if (value.properties.containsKey(id.value))
                    {
                        return value.properties.get(id.value);
                    }
                }

                throw new InvalidComputedObjectKeyType();
            }

            return NullValue.create();
        }

        if (object.type == ValueType.Array && memberExpr.computed)
        {
            ArrayValue value = (ArrayValue) object;

            RuntimeValue member = Interpreter.evaluate(memberExpr.property, env);

            if (member.type == ValueType.Numeric)
            {
                NumericValue key = (NumericValue) member;

                if (!key.isInteger)
                {
                    throw new InvalidArrayIndexTypeException();
                }

                int index = (int)key.value;
                if (value.items.containsKey(index))
                {
                    return value.items.get(index);
                }

                return NullValue.create();
            }

            throw new InvalidArrayIndexTypeException();
        }

        throw new InvalidNodeException("Esperávamos um objeto ou lista para buscarmos uma chave dele.");
    }

    public static RuntimeValue evaluateCallExpression(
        CallExpr call, Environment env) throws AlreadyDeclaredVariableException
    {
        ArrayList<RuntimeValue> args = new ArrayList<>();

        for (Expr expr : call.arguments)
        {
            args.add(Interpreter.evaluate(expr, env));
        }

        RuntimeValue caller = Interpreter.evaluate(call.caller, env);

        if (caller.type == ValueType.Function)
        {
            FunctionValue function = (FunctionValue) caller;
            Environment scope = Environment.create(function.declarationEnv);

            if (function.parameters.size() != call.arguments.size())
            {
                throw new IncorrectNumberOfArgumentsException(String.format("A função %s esperava %d argumento(s), mas recebeu %d.",
                    function.name,
                    function.parameters.size(),
                    call.arguments.size()));
            }

            for (int i = 0; i < function.parameters.size(); i++)
            {
                ArgumentMetadata param = function.parameters.get(i);
                String name = param.getName();

                if (!TypeChecker.check(env, args.get(i), param.getType())) {
                    throw new RuntimeException("Invalid Argument type");
                }

                scope.declareVariable(name, args.get(i), param.getType(), false);
            }

            RuntimeValue result = NullValue.create();
            for (Statement statement : function.body)
            {
                result = Interpreter.evaluate(statement, scope);
            }

            if (!TypeChecker.check(env, result, function.returnType)) {
                throw new ExpectedTypeNotMatch("Tipo de retorno não condiz com o tipo esperado.");
            }

            return result;
        }

        if (caller.type == ValueType.NativeFunction) {
            NativeFunctionValue nativeFunction = (NativeFunctionValue) caller;
            ParameterMetadata parameters = ParameterMetadata.create(args, env);
            return nativeFunction.call.apply(parameters);
        }

        throw new InvalidCallException("Valor informado não permite ser chamado como uma função.");
    }
}
