package Ast.Statements.Types.Primitive;

import Ast.Statements.Types.SymbolType;
import Constants.ReservedKeys;

public class IntegerType extends SymbolType
{
    protected IntegerType()
    {
        super(ReservedKeys.Integer);
    }

    public static IntegerType create()
    {
        return new IntegerType();
    }

    @Override
    public String print(int level)
    {
        return super.print(level);
    }
}
