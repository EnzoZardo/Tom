package Runtime.Evaluate.Factory.BinaryExpr.String;

import Entities.Abstractions.Evaluate.Strategies.StringBinaryExprStrategy;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Exceptions.Evaluate.InvalidStringOperation;
import Runtime.Evaluate.Strategies.BinaryExpr.String.StringAdditionStrategy;
import Runtime.Evaluate.Strategies.BinaryExpr.String.StringDivisionStrategy;
import Runtime.Evaluate.Strategies.BinaryExpr.String.StringMultiplicationStrategy;

public abstract class StringBinaryExprFactory
{
    public static ErrorOr<StringBinaryExprStrategy> build(String operator)
    {
        if (ReservedKeys.Plus.equals(operator))
            return ErrorOr.Success(new StringAdditionStrategy());

        if (ReservedKeys.Multiplication.equals(operator))
            return ErrorOr.Success(new StringMultiplicationStrategy());

        if (ReservedKeys.Division.equals(operator) || ReservedKeys.IntegerDivision.equals(operator))
            return ErrorOr.Success(new StringDivisionStrategy());

        return ErrorOr.Fail(
            String.format("Operação '%s' não permitida para valores do tipo texto.", operator));
    }
}
