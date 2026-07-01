package Ast.Types.Primitive;

import Ast.Types.SymbolType;
import Entities.Constants.ReservedKeys;

public class EmptyType extends SymbolType
{
    protected EmptyType()
    {
        super(ReservedKeys.Empty);
    }

    public static EmptyType create()
    {
        return new EmptyType();
    }

    @Override
    public String print(int level)
    {
        return super.print(level);
    }
}
