package Ast.Expressions.Literals;

import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Lexer.Tokens.Token;

public class IntegerLiteral extends Expr
{
    public int value;

    protected IntegerLiteral(int value)
    {
        super(NodeType.IntegerValue);
        this.value = value;
    }

    public static IntegerLiteral create(Token token) throws InvalidArgumentException
    {
        return new IntegerLiteral(Integer.parseInt(token.value));
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "value: " + value + ",\n" +
                "\t".repeat(level) + "}";
    }
}
