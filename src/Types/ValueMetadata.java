package Types;

import Ast.Statements.Types.Type;
import Runtime.Types.RuntimeValue;

public class ValueMetadata extends Pair<Type, RuntimeValue>
{
    protected ValueMetadata(Type type, RuntimeValue runtimeValue)
    {
        super(type, runtimeValue);
    }

    public static ValueMetadata create(Type type, RuntimeValue runtimeValue)
    {
        return new ValueMetadata(type,  runtimeValue);
    }

    public Type getType()
    {
        return get0();
    }

    public RuntimeValue getValue()
    {
        return get1();
    }
}
