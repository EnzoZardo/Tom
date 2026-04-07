package Ast.Statements;

import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
import Parser.Parser;

public class PrimaryExpr
{
    public static Expr parse(Parser parser) throws InvalidArgumentException, InvalidTokenException
    {
        return switch (parser.peekType())
        {
            case TokenType.IDENTIFIER -> Identifier.create(parser.consume());
            case TokenType.INTEGER_LITERAL -> IntegerLiteral.create(parser.consume());
            case TokenType.FLOAT_LITERAL -> FloatLiteral.create(parser.consume());
            case TokenType.STRING_LITERAL -> StringLiteral.create(parser.consume());
            case TokenType.OPEN_PARENTHESIS -> PrimaryExpr.parseParenthesis(parser);
            default -> throw new InvalidTokenException(String.format("Unexpected Token '%s'.", parser.peekValue()));
        };
    }

    private static Expr parseParenthesis(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.consume();
        Expr expr = Expr.parse(parser);
        parser.expect(TokenType.CLOSE_PARENTHESIS, String.format("Unexpected token received. Expected ')', received '%s'", parser.peekValue()));
        return expr;
    }
}
