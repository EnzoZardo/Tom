package Runtime.Evaluate.Strategies.BinaryExpr;

import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Evaluate.Strategies.StringBinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Runtime.ValueType;
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
        StringBinaryExprStrategy strategy = StringBinaryExprFactory.build(operator);
        return strategy.evaluate(right, left);
    }
}
