package Runtime.NativeFunctions;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Metadata.ParameterMetadata;
import Runtime.Values.ArrayValue;
import Runtime.Values.NumericValue;

import java.util.ArrayList;
import java.util.HashMap;

public class Interval
{
    private static final int MAX_ARGUMENTS = 3;
    private static final int START_END_ARGUMENTS = 2;
    private static final int MIN_ARGUMENTS = 1;

    private static ArrayValue range(int start, int end, int step)
    {
        HashMap<Integer, RuntimeValue> interval = new HashMap<>();
        for (int i = start; i < end; i += step)
        {
            interval.put(i, NumericValue.create(i, true));
        }

        return ArrayValue.create(interval);
    }

    public static ArrayValue call(ParameterMetadata args)
    {
        if (args.getValues().size() > MAX_ARGUMENTS)
        {
            throw new IncorrectNumberOfArgumentsException("Número incorreto de argumentos passados para a " +
                    "função de intervalo.");
        }

        if (args.getValues().stream().anyMatch(x -> x.type != ValueType.Numeric
            && !((NumericValue) x).isInteger))
        {
            throw new ExpectedTypeNotMatch("Somente números inteiros são aceitos para a função de intervalo.");
        }

        ArrayList<RuntimeValue> values = args.getValues();

        if (values.size() == MIN_ARGUMENTS)
        {
            NumericValue value = (NumericValue) values.getFirst();

            return range(0, (int) value.value, 1);
        }

        if (values.size() == START_END_ARGUMENTS)
        {
            NumericValue start = (NumericValue) values.getFirst();
            NumericValue end = (NumericValue) values.getLast();

            return range((int) start.value, (int) end.value, 1);
        }

        if (values.size() == MAX_ARGUMENTS)
        {
            NumericValue start = (NumericValue) values.getFirst();
            NumericValue end = (NumericValue) values.get(1);
            NumericValue step = (NumericValue) values.getLast();

            return range((int) start.value, (int) end.value, (int) step.value);
        }

        throw new IncorrectNumberOfArgumentsException("Número incorreto de argumentos passados para a " +
                "função de intervalo.");
    }
}
