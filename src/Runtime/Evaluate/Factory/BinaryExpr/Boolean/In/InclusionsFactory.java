package Runtime.Evaluate.Factory.BinaryExpr.Boolean.In;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Values.ArrayValue;
import Runtime.Values.ObjectValue;
import Runtime.Values.StringValue;

public abstract class InclusionsFactory
{
    public static Contains<?> build(RuntimeValue right, RuntimeValue left) {
        return switch (right.type)
        {
            case ValueType.Array -> ArrayInclusionsFactory.build(left, (ArrayValue) right);
            case ValueType.String -> StringInclusionsFactory.build(left, (StringValue) right);
            case ValueType.Object -> ObjectInclusionsFactory.build(left, (ObjectValue) right);
            default -> throw new InvalidBinaryOperation("Só é permitido verificar se um valor está presente em listas, objetos ou textos.");
        };
    }
}
