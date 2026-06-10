package Runtime.NativeFunctions.Convert;

import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Metadata.ParameterMetadata;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.StringValue;

public abstract class ToString
{
    private static final int MAX_ARGUMENTS_SIZE = 1;
    public static StringValue call(ParameterMetadata args)
    {
        if (args.getValues().size() > MAX_ARGUMENTS_SIZE)
        {
            throw new IncorrectNumberOfArgumentsException("Número incorreto de argumentos passados para a " +
                "função de conversão.");
        }

        RuntimeValue arg = args.getValues().getFirst();

        if (arg.type == ValueType.ClassMember)
        {
            arg = ((ClassMemberValue) arg).value;
        }

        return StringValue.create(arg.toString());
    }
}
