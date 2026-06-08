package Runtime.Evaluate.Strategies.BinaryExpr;

import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Entities.Exceptions.Evaluate.InvalidOperatorException;
import Runtime.Evaluate.Factory.BinaryExpr.Boolean.In.InclusionsFactory;
import Runtime.Values.*;

public class BooleanBinaryStrategy implements BinaryExprStrategy
{
    public static boolean canEvaluate(String operator)
    {
        return ReservedOperators.isBooleanOperator(operator);
    }

    @Override
    public RuntimeValue evaluate(RuntimeValue right, RuntimeValue left, String operator)
    {
        return switch (operator)
        {
            case ReservedKeys.In -> InclusionsFactory.build(left, right).has();
            case ReservedKeys.Equality -> BooleanValue.create(left.equals(right));
            case ReservedKeys.Difference -> BooleanValue.create(!left.equals(right));
            case ReservedKeys.Or -> BooleanValue.create(left.bool() || right.bool());
            case ReservedKeys.And -> BooleanValue.create(left.bool() && right.bool());
            case ReservedKeys.Minor,
                 ReservedKeys.Greater,
                 ReservedKeys.MinorOrEqual,
                 ReservedKeys.GreaterOrEqual -> evaluateSizeOperator(left, right, operator);
            default -> throw new InvalidOperatorException(operator);
        };
    }

    private static BooleanValue evaluateSizeOperator(RuntimeValue left, RuntimeValue right, String operator)
    {
        if (left.type != ValueType.Numeric || right.type != ValueType.Numeric)
        {
            throw new InvalidBinaryOperation(String.format("A operação %s só é permitida entre valores numéricos.",
                operator));
        }

        NumericValue rightValue = (NumericValue) right;
        NumericValue leftValue = (NumericValue) left;

        boolean result = switch (operator)
        {
            case ReservedKeys.Minor -> leftValue.value < rightValue.value;
            case ReservedKeys.Greater -> leftValue.value > rightValue.value;
            case ReservedKeys.MinorOrEqual -> leftValue.value <= rightValue.value;
            case ReservedKeys.GreaterOrEqual -> leftValue.value >= rightValue.value;
            default -> throw new InvalidOperatorException(operator);
        };

        return BooleanValue.create(result);
    }
}