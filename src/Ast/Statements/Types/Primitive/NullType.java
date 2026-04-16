package Ast.Statements.Types.Primitive;

import Ast.Statements.Types.SymbolType;
import Constants.ReservedKeys;

public class NullType extends SymbolType
{
    protected NullType()
    {
        super(ReservedKeys.Null);
    }

    public static NullType create()
    {
        return new NullType();
    }

    @Override
    public String print(int level)
    {
        return super.print(level);
    }
}
