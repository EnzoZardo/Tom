package Runtime.Evaluate.Strategies.BinaryExpr.String;

import Entities.Abstractions.Evaluate.Strategies.StringBinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidStringOperation;
import Entities.Exceptions.Evaluate.ZeroDivisionException;
import Runtime.Values.ArrayValue;
import Runtime.Values.NumericValue;
import Runtime.Values.StringValue;

import java.util.HashMap;

public class StringDivisionStrategy implements StringBinaryExprStrategy {
    @Override
    public RuntimeValue evaluate(RuntimeValue right, RuntimeValue left) {
        final String error = "Operação de divisão só é permitida entre texto e inteiro.";
        if (left.type != ValueType.String || right.type != ValueType.Numeric) {
            throw new InvalidStringOperation(error);
        }

        StringValue leftValue = (StringValue) left;
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

        for (int i = 0; i < divisor; i++)
        {
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
