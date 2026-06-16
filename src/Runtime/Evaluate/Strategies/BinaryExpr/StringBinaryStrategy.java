package Runtime.Evaluate.Strategies.BinaryExpr;

import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Evaluate.Strategies.StringBinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Factory.BinaryExpr.String.StringBinaryExprFactory;

public class StringBinaryStrategy implements BinaryExprStrategy
{
    public static boolean canEvaluate(RuntimeValue right, RuntimeValue left, String operator)
    {
        return ReservedOperators.isNumericOperator(operator)
            && (left.type == ValueType.String || right.type == ValueType.String);
    }

    @Override
    public RuntimeValue evaluate(RuntimeValue right, RuntimeValue left, String operator)
    {
        ErrorOr<StringBinaryExprStrategy> result = StringBinaryExprFactory.build(operator);

        if (result.isError()) throw new InvalidBinaryOperation(result.error.getMessage());

        StringBinaryExprStrategy strategy = result.value;
        return strategy.evaluate(right, left);
    }
}
