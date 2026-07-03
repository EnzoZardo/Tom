package Entities.Abstractions;

import Ast.Types.BinaryType;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
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
        return BinaryType.parse(parser);
    }

    public static Type reduce(Environment env, Type type)
    {
        if (type.type == TypeKind.NativeFunction)
        {
            return type;
        }

        return BinaryType.reduce(env, type);
    }

    public static ErrorOr<Void> equals(Type type1, Type type2)
    {
        if (type1.type != type2.type)
        {
            return ErrorOr.Fail("Os tipos são diferentes.");
        }

        return BinaryType.equals(type1, type2);
    }

    public abstract String print(int level);

    @Override
    public String toString()
    {
        return this.print(0);
    }
}
