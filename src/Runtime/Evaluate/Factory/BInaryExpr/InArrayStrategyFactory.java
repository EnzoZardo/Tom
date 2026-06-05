package Runtime.Evaluate.Factory.BInaryExpr;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Strategies.Operators.In.Array.*;
import Runtime.Values.ArrayValue;

public abstract class InArrayStrategyFactory
{
    public static InArrayBase build(RuntimeValue left, ArrayValue right)
    {
        return switch (left.type) {
            case ValueType.Numeric -> new InArrayNumeric(right);
            case ValueType.String -> new InArrayString(right);
            case ValueType.Object -> new InArrayObject(right);
            case ValueType.Array -> new InArrayArray(right);
            case ValueType.Boolean -> new InArrayBoolean(right);
            case ValueType.Null -> new InArrayNull(right);
            default -> throw new InvalidBinaryOperation("Valor não permitido para ser verificado se está em lista.");
        };
    }
}
