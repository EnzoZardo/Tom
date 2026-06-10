package Runtime.NativeFunctions;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Metadata.ParameterMetadata;
import Runtime.Values.ArrayValue;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.NumericValue;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Interval
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

    private static ArrayValue reverseRange(int start, int end, int step)
    {
        HashMap<Integer, RuntimeValue> interval = new HashMap<>();
        for (int i = start; i > end; i += step)
        {
            interval.put(i, NumericValue.create(i, true));
        }

        return ArrayValue.create(interval);
    }

    private static boolean validateArg(RuntimeValue value)
    {
        if (value.type == ValueType.ClassMember)
        {
            ClassMemberValue member = (ClassMemberValue) value;

            return validateArgSource(member.value);
        }

        return validateArgSource(value);
    }

    private static boolean validateArgSource(RuntimeValue value)
    {
        if (value.type != ValueType.Numeric)
        {
            return true;
        }

        NumericValue numeric = (NumericValue) value;
        return !numeric.isInteger;
    }

    public static ArrayValue call(ParameterMetadata args)
    {
        //TODO: verify
        if (args.getValues().size() > MAX_ARGUMENTS)
        {
            throw new IncorrectNumberOfArgumentsException("Número incorreto de argumentos passados para a " +
                    "função de intervalo.");
        }

        if (args
            .getValues()
            .stream()
            .anyMatch(Interval::validateArg))
        {
            throw new ExpectedTypeNotMatch("Apenas números inteiros são permitidos para a função de intervalo.");
        }

        ArrayList<RuntimeValue> values = args.getValues();

        if (values.size() == MIN_ARGUMENTS)
        {
            RuntimeValue arg = values.getFirst();
            if (arg.type == ValueType.ClassMember)
            {
                arg = ((ClassMemberValue) arg).value;
            }

            NumericValue value = (NumericValue) arg;

            return range(0, (int) value.value, 1);
        }

        if (values.size() == START_END_ARGUMENTS)
        {
            RuntimeValue arg1 = values.getFirst();
            if (arg1.type == ValueType.ClassMember)
            {
                arg1 = ((ClassMemberValue) arg1).value;
            }

            RuntimeValue arg2 = values.getFirst();
            if (arg2.type == ValueType.ClassMember)
            {
                arg2 = ((ClassMemberValue) arg2).value;
            }

            NumericValue start = (NumericValue) arg1;
            NumericValue end = (NumericValue) arg2;

            return range((int) start.value, (int) end.value, 1);
        }

        if (values.size() == MAX_ARGUMENTS)
        {
            RuntimeValue arg1 = values.getFirst();
            if (arg1.type == ValueType.ClassMember)
            {
                arg1 = ((ClassMemberValue) arg1).value;
            }

            RuntimeValue arg2 = values.get(1);
            if (arg2.type == ValueType.ClassMember)
            {
                arg2 = ((ClassMemberValue) arg2).value;
            }

            RuntimeValue arg3 = values.getLast();
            if (arg3.type == ValueType.ClassMember)
            {
                arg3 = ((ClassMemberValue) arg3).value;
            }

            NumericValue start = (NumericValue) arg1;
            NumericValue end = (NumericValue) arg2;
            NumericValue step = (NumericValue) arg3;

            if (start.value < end.value
                && start.value < 0
                && step.value < 0)
            {
                return reverseRange((int) start.value, (int) end.value, (int) step.value);
            }

            if (start.value > end.value && step.value < 0)
            {
                return reverseRange((int) start.value, (int) end.value, (int) step.value);
            }

            if (start.value < end.value
                && start.value > 0
                && step.value < 0)
            {
                return ArrayValue.create();
            }

            return range((int) start.value, (int) end.value, (int) step.value);
        }

        throw new IncorrectNumberOfArgumentsException("Número incorreto de argumentos passados para a " +
                "função de intervalo.");
    }
}
