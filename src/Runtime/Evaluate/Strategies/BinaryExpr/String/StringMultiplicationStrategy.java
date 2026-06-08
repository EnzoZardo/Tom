package Runtime.Evaluate.Strategies.BinaryExpr.String;

import Entities.Abstractions.Evaluate.Strategies.StringBinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidStringOperation;
import Runtime.Values.NumericValue;
import Runtime.Values.StringValue;

public class StringMultiplicationStrategy implements StringBinaryExprStrategy {
    @Override
    public RuntimeValue evaluate(RuntimeValue right, RuntimeValue left)     {
        final String message = "Não se pode multiplicar um texto por um valor não inteiro";
        if (left.type == ValueType.Numeric) {
            StringValue rightValue = (StringValue) right;
            NumericValue leftValue = (NumericValue) left;

            if (!leftValue.isInteger) {
                throw new InvalidStringOperation(message);
            }

            return StringValue.create(rightValue.value.repeat((int) leftValue.value));
        }

        if (right.type == ValueType.Numeric) {
            NumericValue rightValue = (NumericValue) right;
            StringValue leftValue = (StringValue) left;

            if (!rightValue.isInteger) {
                throw new InvalidStringOperation(message);
            }

            return StringValue.create(leftValue.value.repeat((int) rightValue.value));
        }

        throw new InvalidStringOperation("A operação de multiplicação "
            + "de texto não é permitida para os valores informados.");
    }
}
