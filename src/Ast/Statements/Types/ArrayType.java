package Ast.Statements.Types;

import Ast.Enums.TypeKind;
import Lexer.Types.Enums.TokenType;
import Parser.Parser;

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
