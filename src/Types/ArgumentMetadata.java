package Types;

import Ast.Statements.Types.Type;

public class ArgumentMetadata extends Pair<Type, String>
{
    protected ArgumentMetadata(Type type, String name)
    {
        super(type, name);
    }

    public static ArgumentMetadata create(Type type, String name)
    {
        return new ArgumentMetadata(type, name);
    }

    public Type getType()
    {
        return get0();
    }

    public String getName()
    {
        return get1();
    }
}