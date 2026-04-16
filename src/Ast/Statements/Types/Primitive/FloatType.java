package Ast.Statements.Types.Primitive;

import Ast.Statements.Types.SymbolType;
import Constants.ReservedKeys;

public class FloatType extends SymbolType
{
    protected FloatType()
    {
        super(ReservedKeys.Float);
    }

    public static FloatType create()
    {
        return new FloatType();
    }

    @Override
    public String print(int level)
    {
        return super.print(level);
    }
}