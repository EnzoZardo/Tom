package Ast.Statements;

import Ast.Types.Enums.NodeType;
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
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "text: " + text + ",\n" +
                "\t".repeat(level) + "}";
    }
}
