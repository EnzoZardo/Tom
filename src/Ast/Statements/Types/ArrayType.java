package Ast.Statements.Types;

import Ast.Enums.TypeKind;
import Lexer.Types.Enums.TokenType;
import Parser.Parser;
import Runtime.Environment;

public class ArrayType extends Type
{
    public Type underlying;

    protected ArrayType(Type underlying)
    {
        super(TypeKind.ArrayType);
        this.underlying = underlying;
    }

    public static ArrayType create(Type underlying)
    {
        return new ArrayType(underlying);
    }

    public static Type reduce(Environment env, Type type)
    {
        if (type.type != TypeKind.ArrayType)
        {
            return SymbolType.reduce(env, type);
        }

        Type underlying = ((ArrayType) type).underlying;
        return ArrayType.create(Type.reduce(env, underlying));
    }

    public static Type parse(Parser parser)
    {
        if (!parser.peekIs(TokenType.OPEN_BRACKETS))
        {
            return SymbolType.parse(parser);
        }
        parser.consume();
        parser.expect(TokenType.CLOSE_BRACKETS, "Expecting ']' after open brackets on array type parsing.");

        return ArrayType.create(Type.parse(parser));
    }

    public static boolean equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.ArrayType) {
            return SymbolType.equals(type1, type2);
        }

        ArrayType array1 = (ArrayType) type1;
        ArrayType array2 = (ArrayType) type2;

        return Type.equals(array1.underlying, array2.underlying);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "underlying: " + underlying.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
