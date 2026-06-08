package Runtime.Evaluate.Factory.BinaryExpr.Boolean.In;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Strategies.BinaryExpr.In.Contains;
import Runtime.Evaluate.Strategies.BinaryExpr.In.String.StringContainsString;
import Runtime.Values.StringValue;

public abstract class StringInclusionsFactory {
    public static ErrorOr<Contains<StringValue>> build(RuntimeValue left, StringValue right) {
        if (left.type == ValueType.String) {
            return ErrorOr.Success(new StringContainsString(right, (StringValue) left));
        }

        return ErrorOr.Fail("Valor não permitido para ser verificado se está em texto.");
    }
}