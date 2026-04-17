package Ast.Statements.Types;

import Ast.Enums.TypeKind;
import Constants.ReservedPrimitiveTypes;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
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
        if (!ReservedPrimitiveTypes.isReserved(symbolType.value)) {
            return env.lookupType(symbolType.value);
        }

        return symbolType;
    }

    public static Type parse(Parser parser)
    {
        Token token = parser.expect(TokenType.IDENTIFIER, "Expecting type identifier on type parsing.");
        return SymbolType.create(token.value);
    }

    public static boolean equals(Type type1, Type type2)
    {
        SymbolType symbol1 = (SymbolType) type1;
        SymbolType symbol2 = (SymbolType) type2;
        return symbol1.value.equals(symbol2.value);
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
