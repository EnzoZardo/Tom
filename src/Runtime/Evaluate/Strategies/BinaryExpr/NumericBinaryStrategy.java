package Runtime.Evaluate.Strategies.BinaryExpr;

import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidOperatorException;
import Entities.Exceptions.Evaluate.ZeroDivisionException;
import Runtime.Values.NumericValue;

public class NumericBinaryStrategy implements BinaryExprStrategy
{
    public static boolean canEvaluate(RuntimeValue right, RuntimeValue left, String operator) {
        return ReservedOperators.isNumericOperator(operator)
            && left.type == ValueType.Numeric
            && right.type == ValueType.Numeric;
    }

    @Override
    public RuntimeValue evaluate(RuntimeValue right, RuntimeValue left, String operator) {
        NumericValue rightNumeric = (NumericValue) right;
        NumericValue leftNumeric = (NumericValue) left;

        float result = switch (operator) {
            case ReservedKeys.IntegerDivision -> (int) evaluateDivision(leftNumeric.value, rightNumeric.value);
            case ReservedKeys.Division -> evaluateDivision(leftNumeric.value, rightNumeric.value);
            case ReservedKeys.Multiplication -> leftNumeric.value * rightNumeric.value;
            case ReservedKeys.Minus -> leftNumeric.value - rightNumeric.value;
            case ReservedKeys.Plus -> leftNumeric.value + rightNumeric.value;
            case ReservedKeys.Mod -> leftNumeric.value % rightNumeric.value;
            default -> throw new InvalidOperatorException(operator);
        };

        boolean isFloat = !operator.equals(ReservedKeys.IntegerDivision);
        return NumericValue.create(result, leftNumeric.isInteger && rightNumeric.isInteger && isFloat);
    }

    private static float evaluateDivision(Number left, Number right) {
        ZeroDivisionException.ThrowIfZero(left);
        return left.floatValue() / right.floatValue();
    }
}
