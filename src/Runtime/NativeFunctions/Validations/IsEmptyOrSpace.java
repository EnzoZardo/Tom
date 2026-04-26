package Runtime.NativeFunctions.Validations;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Metadata.ParameterMetadata;
import Runtime.NativeObjects.StringObject;
import Runtime.Values.BooleanValue;
import Runtime.Values.StringValue;

public class IsEmptyOrSpace
{
    private static final int MAX_ARGUMENTS_SIZE = 1;
    public static BooleanValue call(ParameterMetadata args)
    {
        if (args.getValues().size() > MAX_ARGUMENTS_SIZE)
        {
            throw new IncorrectNumberOfArgumentsException("Número incorreto de argumentos passados para a " +
                "função de conversão.");
        }

        RuntimeValue arg = args.getValues().getFirst();

        if (arg.type != ValueType.String)
        {
            throw new ExpectedTypeNotMatch("Esperávamos um texto para verificar se está vazio.");
        }

        StringValue value = (StringValue) arg;

        return BooleanValue.create(StringObject.SPACE_VALUE.equals(value.value)
            || StringObject.EMPTY_VALUE.equals(value.value));
    }
}
