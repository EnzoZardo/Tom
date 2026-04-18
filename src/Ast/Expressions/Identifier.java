package Ast.Expressions;

import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;

public class Identifier extends Expr
{
    public String value;

    protected Identifier(String value)
    {
        this.value = value;
        super(NodeType.Identifier);
    }

    public static Identifier create(Token token) throws InvalidArgumentException
    {
        if (token.type != TokenType.IDENTIFIER)
        {
            throw new InvalidArgumentException("Um símbolo inesperado foi dado como identificador.");
        }

        return new Identifier(token.value);
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "value: " + value + ",\n" +
                "\t".repeat(level) + "}";
    }
}
