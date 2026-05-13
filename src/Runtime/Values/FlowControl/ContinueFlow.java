package Runtime.Values.FlowControl;

import Entities.Abstractions.Runtime.RuntimeException;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Entities.Exceptions.Evaluate.InvalidStatementUseException;

public class ContinueFlow extends RuntimeException
{
    protected ContinueFlow()
    {
        super(ValueType.Continue);
    }

    public static ContinueFlow create()
    {
        return new ContinueFlow();
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
        throw new InvalidBinaryOperation("Não se pode testar a igualdade entre um operador continue e outro valor.");
    }

    @Override
    public boolean bool()
    {
        throw new InvalidStatementUseException("Operador não pode ser convertido para lógico");
    }
}