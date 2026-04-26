package Runtime.NativeFunctions.Convert;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Metadata.ParameterMetadata;
import Runtime.Values.NumericValue;
import Runtime.Values.StringValue;

public class ToInteger
{
    private static final int MAX_ARGUMENTS_SIZE = 1;
    public static NumericValue call(ParameterMetadata args)
    {
        if (args.getValues().size() > MAX_ARGUMENTS_SIZE)
        {
            throw new IncorrectNumberOfArgumentsException("Número incorreto de argumentos passados para a " +
                    "função de conversão.");
        }

        RuntimeValue arg = args.getValues().getFirst();

        if (arg.type != ValueType.String)
        {
            throw new ExpectedTypeNotMatch("Esperávamos um texto para converter para inteiro.");
        }

        StringValue value = (StringValue) arg;

        try
        {
            return NumericValue.create((float) Integer.parseInt(value.value), true);
        }
        catch (NumberFormatException e)
        {
            throw new ExpectedTypeNotMatch("O texto informado não pode ser convertido para inteiro.");
        }
    }
}
