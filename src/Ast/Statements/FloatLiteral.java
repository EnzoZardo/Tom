package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Lexer.Types.Token;

public class FloatLiteral extends Expr
{
    public float number;

    protected FloatLiteral(float number)
    {
        super(NodeType.FloatValue);
        this.number = number;
    }

    public static FloatLiteral create(Token token) throws InvalidArgumentException
    {
        return new FloatLiteral(Float.parseFloat(token.value));
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "number: " + number + ",\n" +
                "\t".repeat(level) + "}";
    }
}
