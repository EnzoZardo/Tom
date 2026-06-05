package Runtime.Evaluate.Strategies;

import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedOperators;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidStringOperation;
import Entities.Exceptions.Evaluate.ZeroDivisionException;
import Runtime.Values.ArrayValue;
import Runtime.Values.NumericValue;
import Runtime.Values.StringValue;

import java.util.HashMap;

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
        if (ReservedKeys.Plus.equals(operator))
        {
            return StringValue.create(left.toString() + right.toString());
        };

        if (ReservedKeys.Multiplication.equals(operator))
        {
            return multiplication(right, left);
        }

        if (ReservedKeys.Division.equals(operator) || ReservedKeys.IntegerDivision.equals(operator))
        {
            return division(right, left);
        }

        throw new InvalidStringOperation(String.format("Operação '%s' não permitida para valores do tipo texto.",
            operator));
    }

    private StringValue multiplication(RuntimeValue right, RuntimeValue left) {
        final String message = "Não se pode multiplicar um texto por um valor não inteiro";
        if (left.type == ValueType.Numeric)
        {
            StringValue rightValue = (StringValue) right;
            NumericValue leftValue = (NumericValue) left;

            if (!leftValue.isInteger)
            {
                throw new InvalidStringOperation(message);
            }

            return StringValue.create(rightValue.value.repeat((int) leftValue.value));
        }

        if (right.type == ValueType.Numeric)
        {
            NumericValue rightValue = (NumericValue) right;
            StringValue leftValue = (StringValue) left;

            if (!rightValue.isInteger)
            {
                throw new InvalidStringOperation(message);
            }

            return StringValue.create(leftValue.value.repeat((int) rightValue.value));
        }

        throw new InvalidStringOperation("A operação de multiplicação "
            + "de texto não é permitida para os valores informados.");
    }

    private ArrayValue division(RuntimeValue right, RuntimeValue left)
    {
        final String error = "Operação de divisão só é permitida entre texto e inteiro.";
        if (left.type != ValueType.String || right.type != ValueType.Numeric)
        {
            throw new InvalidStringOperation(error);
        }

        StringValue leftValue  = (StringValue) left;
        NumericValue rightValue = (NumericValue) right;

        if (!rightValue.isInteger)
        {
            throw new InvalidStringOperation(error);
        }

        int divisor = (int) rightValue.value;
        ZeroDivisionException.ThrowIfZero(divisor);

        String target = leftValue.value;

        if (divisor > target.length())
        {
            throw new InvalidStringOperation("Não se pode dividir um texto por um tamanho maior do que o seu.");
        }

        HashMap<Integer, RuntimeValue> items = new HashMap<>();
        int len = target.length();
        int size = Math.floorDiv(len, divisor);
        int res = len % divisor;

        int start = 0, index = 0;

        for (int i = 0; i < divisor; i++) {
            int partSize = size + (i < res ? 1 : 0);
            int end = start + partSize;
            String value = target.substring(start, end);
            items.put(index, StringValue.create(value));
            start = end;
            index++;
        }

        return ArrayValue.create(items);
    }
}
