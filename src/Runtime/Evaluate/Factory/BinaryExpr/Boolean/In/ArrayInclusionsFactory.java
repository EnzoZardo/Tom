package Runtime.Evaluate.Factory.BinaryExpr.Boolean.In;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Array.*;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Values.*;

public abstract class ArrayInclusionsFactory
{
    public static Contains<ArrayValue> build(RuntimeValue left, ArrayValue right)
    {
        return switch (left.type) {
            case ValueType.Null -> new ArrayContainsNull(right);
            case ValueType.Array -> new ArrayContainsArray(right, (ArrayValue) left);
            case ValueType.Object -> new ArrayContainsObject(right, (ObjectValue) left);
            case ValueType.String -> new ArrayContainsString(right, (StringValue) left);
            case ValueType.Numeric -> new ArrayContainsNumeric(right, (NumericValue) left);
            case ValueType.Boolean -> new ArrayContainsBoolean(right, (BooleanValue) left);
            default -> throw new InvalidBinaryOperation("Valor não permitido para ser verificado se está em lista.");
        };
    }
}
