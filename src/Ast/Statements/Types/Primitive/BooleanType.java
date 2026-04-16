package Ast.Statements.Types.Primitive;

import Ast.Statements.Types.SymbolType;
import Constants.ReservedKeys;

public class BooleanType extends SymbolType
{
    protected BooleanType()
    {
        super(ReservedKeys.Boolean);
    }

    public static BooleanType create()
    {
        return new BooleanType();
    }

    @Override
    public String print(int level)
    {
        return super.print(level);
    }
}
