package Ast.Expressions;

import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class Identifier extends Expr
{
    public String value;

    protected Identifier(String value)
    {
        this.value = value;
        super(NodeType.Identifier);
    }

    public static Expr parse(Parser parser)
    {
        Token token = parser.consume();
        Identifier identifier = new Identifier(token.value);
        if (parser.peekIs(TokenType.BINARY_OPERATOR)
            && ReservedKeys.Minor.equals(parser.peekValue()))
        {
            return CallExpr.parse(parser, identifier);
        }

        return identifier;
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
