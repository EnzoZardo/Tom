package Runtime.Evaluate.Factory.BinaryExpr.Boolean.In;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Object.ObjectContainsArray;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Object.ObjectContainsString;
import Runtime.Values.ArrayValue;
import Runtime.Values.ObjectValue;
import Runtime.Values.StringValue;

public abstract class ObjectInclusionsFactory
{
    public static Contains<ObjectValue> build(RuntimeValue left, ObjectValue right)
    {
        return switch (left.type) {
            case ValueType.String -> new ObjectContainsString(right, (StringValue) left);
            case ValueType.Array -> new ObjectContainsArray(right, (ArrayValue) left);
            default -> throw new InvalidBinaryOperation("Valor não permitido para ser verificado se está em objeto.");
        };
    }
}