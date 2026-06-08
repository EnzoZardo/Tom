package Runtime.Evaluate.Strategies.UnaryExpr;

import Entities.Abstractions.Evaluate.Strategies.UnaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidUnaryExpression;
import Runtime.Values.NumericValue;

public class AdditiveUnaryStrategy implements UnaryExprStrategy
{
    @Override
    public RuntimeValue evaluate(RuntimeValue right, String operator)
    {
        if (right.type != ValueType.Numeric)
        {
            throw new InvalidUnaryExpression();
        }

        NumericValue val = (NumericValue) right;

        if (ReservedKeys.Minus.equals(operator)) {
            return val.opposite();
        }

        return val;
    }
}
