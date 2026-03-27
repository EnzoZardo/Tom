package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;

public class NumericLiteral extends Expr
{
    public Number number;

    protected NumericLiteral(Number number)
    {
        super(NodeType.NumericLiteral);
        this.number = number;
    }

    public static NumericLiteral create(Token token) throws InvalidArgumentException
    {
        if (token.type != TokenType.NUMERIC || token.value.isEmpty())
        {
            throw new InvalidArgumentException("Invalid NumericLiteral token was given.");
        }

        if (_isInteger(token.value))
        {
            return new NumericLiteral(Integer.parseInt(token.value));
        }

        return new NumericLiteral(Double.parseDouble(token.value));
    }

    private static boolean _isInteger(String symbol)
    {
        try
        {
            Integer.parseInt(symbol);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
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
