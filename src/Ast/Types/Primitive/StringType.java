package Ast.Types.Primitive;

import Ast.Types.SymbolType;
import Entities.Constants.ReservedKeys;

public class StringType extends SymbolType
{
    protected StringType()
    {
        super(ReservedKeys.String);
    }

    public static StringType create()
    {
        return new StringType();
    }

    @Override
    public String print(int level)
    {
        return super.print(level);
    }
}
