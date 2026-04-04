package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Lexer.Types.Token;

public class IntegerLiteral extends Expr
{
    public int number;

    protected IntegerLiteral(int number)
    {
        super(NodeType.IntegerValue);
        this.number = number;
    }

    public static IntegerLiteral create(Token token) throws InvalidArgumentException
    {
        return new IntegerLiteral(Integer.parseInt(token.value));
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
