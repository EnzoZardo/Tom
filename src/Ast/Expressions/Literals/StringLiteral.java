package Ast.Expressions.Literals;

import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Lexer.Tokens.Token;

public class StringLiteral extends Expr
{
    public final String value;

    protected StringLiteral(String value)
    {
        this.value = value;
        super(NodeType.StringLiteral);
    }

    public static StringLiteral create(Token token)
    {
        return new StringLiteral(token.value);
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
