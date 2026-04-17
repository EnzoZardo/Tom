package Ast.Types.Primitive;

import Ast.Types.SymbolType;
import Entities.Constants.ReservedKeys;

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
