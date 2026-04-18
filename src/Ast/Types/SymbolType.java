package Ast.Types;

import Entities.Common.Result.ResultVoid;
import Entities.Constants.ReservedPrimitiveTypes;
import Entities.Enums.TypeKind;
import Entities.Abstractions.Type;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;
import Runtime.Environment;

public class SymbolType extends Type
{
    public String value;
    protected SymbolType(String value)
    {
        super(TypeKind.SymbolType);
        this.value = value;
    }

    public static SymbolType create(String value)
    {
        return new SymbolType(value);
    }

    public static Type reduce(Environment env, Type type)
    {
        SymbolType symbolType = (SymbolType) type;

        if (!ReservedPrimitiveTypes.isReserved(symbolType.value))
        {
            return env.lookupType(symbolType.value);
        }

        return symbolType;
    }

    public static Type parse(Parser parser)
    {
        Token token = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do tipo enquanto analisávamos.");
        return SymbolType.create(token.value);
    }

    public static ResultVoid equals(Type type1, Type type2)
    {
        SymbolType symbol1 = (SymbolType) type1;
        SymbolType symbol2 = (SymbolType) type2;

        if (symbol1.value.equals(symbol2.value))
        {
            return ResultVoid.Ok();
        }

        return ResultVoid.Fail("Os símbolos dos tipos diferem.");
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "symbol: " + value + ",\n" +
                "\t".repeat(level) + "}";
    }
}
