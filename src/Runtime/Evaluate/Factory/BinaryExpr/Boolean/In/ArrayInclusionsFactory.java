package Runtime.Evaluate.Factory.BinaryExpr.Boolean.In;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Array.*;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Values.*;

public abstract class ArrayInclusionsFactory {
    public static ErrorOr<Contains<ArrayValue>> build(RuntimeValue left, ArrayValue right) {
        return switch (left.type) {
            case Null -> ErrorOr.Success(new ArrayContainsNull(right));
            case Array -> ErrorOr.Success(new ArrayContainsArray(right, (ArrayValue) left));
            case Object -> ErrorOr.Success(new ArrayContainsObject(right, (ObjectValue) left));
            case String -> ErrorOr.Success(new ArrayContainsString(right, (StringValue) left));
            case Numeric -> ErrorOr.Success(new ArrayContainsNumeric(right, (NumericValue) left));
            case Boolean -> ErrorOr.Success(new ArrayContainsBoolean(right, (BooleanValue) left));
            default -> ErrorOr.Fail("Valor não permitido para ser verificado se está em lista.");
        };
    }
}
