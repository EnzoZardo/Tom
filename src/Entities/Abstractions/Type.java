package Entities.Abstractions;

import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.TypeKind;
import Ast.Types.FunctionType;
import Parser.Parser;
import Runtime.Environment;

public abstract class Type extends Statement
{
    public TypeKind type;

    public Type(TypeKind type)
    {
        super(NodeType.TypeDeclaration);
        this.type = type;
    }

    public static Type parse(Parser parser)
    {
        return FunctionType.parse(parser);
    }

    public static Type reduce(Environment env, Type type)
    {
        return FunctionType.reduce(env, type);
    }

    public static boolean equals(Type type1, Type type2)
    {
        if (type1.type != type2.type)
        {
            return false;
        }

        return FunctionType.equals(type1, type2);
    }

    public abstract String print(int level);

    @Override
    public String toString()
    {
        return this.print(0);
    }
}
