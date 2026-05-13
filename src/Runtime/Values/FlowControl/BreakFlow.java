package Runtime.Values.FlowControl;

import Entities.Abstractions.Runtime.RuntimeException;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Entities.Exceptions.Evaluate.InvalidStatementUseException;

public class BreakFlow extends RuntimeException
{
    protected BreakFlow()
    {
        super(ValueType.Break);
    }

    public static BreakFlow create()
    {
        return new BreakFlow();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public boolean equals(RuntimeValue that)
    {
        throw new InvalidBinaryOperation("Não se pode testar a igualdade entre um operador pare e outro valor.");
    }

    @Override
    public boolean bool()
    {
        throw new InvalidStatementUseException("Operador não pode ser convertido para lógico");
    }
}