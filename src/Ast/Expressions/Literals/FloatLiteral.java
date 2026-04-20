package Ast.Expressions.Literals;

import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Lexer.Tokens.Token;

public class FloatLiteral extends Expr
{
    public float value;

    protected FloatLiteral(float value)
    {
        super(NodeType.FloatLiteral);
        this.value = value;
    }

    public static FloatLiteral create(Token token) throws InvalidArgumentException
    {
        return new FloatLiteral(Float.parseFloat(token.value));
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
