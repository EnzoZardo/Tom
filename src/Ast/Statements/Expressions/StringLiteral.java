package Ast.Statements.Expressions;

import Ast.Enums.NodeType;
import Lexer.Types.Token;

public class StringLiteral extends Expr
{
    public final String text;

    protected StringLiteral(String text)
    {
        this.text = text;
        super(NodeType.StringValue);
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
                "\t".repeat(next) + "text: " + text + ",\n" +
                "\t".repeat(level) + "}";
    }
}
