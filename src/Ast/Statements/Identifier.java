package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;

public class Identifier extends Expr
{
    protected final String symbol;

    protected Identifier(String symbol)
    {
        this.symbol = symbol;
        super(NodeType.Identifier);
    }

    public static Identifier create(Token token) throws InvalidArgumentException
    {
        if (token.type != TokenType.IDENTIFIER)
        {
            throw new InvalidArgumentException("Invalid Identifier token was given.");
        }
        return new Identifier(token.value);
    }

    public String get()
    {
        return symbol;
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "symbol: " + symbol + ",\n" +
                "\t".repeat(level) + "}";
    }
}
