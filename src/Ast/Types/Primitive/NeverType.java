package Ast.Types.Primitive;

import Entities.Abstractions.Type;
import Entities.Enums.TypeKind;

public class NeverType extends Type
{
    protected NeverType()
    {
        super(TypeKind.NeverType);
    }

    public static NeverType create()
    {
        return new NeverType();
    }

    @Override
    public String print(int level)
    {
        return "Nunca";
    }
}
