package Ast.Expressions;

import Ast.Expressions.Literals.*;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Entities.Enums.Lexer.TokenType;
import Parser.Parser;

public class PrimaryExpr
{
    public static Expr parse(Parser parser) throws InvalidArgumentException
    {
        return switch (parser.peekType())
        {
            case TokenType.IDENTIFIER -> Identifier.create(parser.consume());
            case TokenType.FLOAT_LITERAL -> FloatLiteral.create(parser.consume());
            case TokenType.OPEN_PARENTHESIS -> PrimaryExpr.parseParenthesis(parser);
            case TokenType.STRING_LITERAL -> StringLiteral.create(parser.consume());
            case TokenType.INTEGER_LITERAL -> IntegerLiteral.create(parser.consume());
            case TokenType.OPEN_BRACE -> ObjectLiteral.parse(parser);
            case TokenType.OPEN_BRACKETS -> ArrayLiteral.parse(parser);
            default -> throw new InvalidTokenException(String.format("Símbolo inesperado '%s'", parser.peekValue()));
        };
    }

    private static Expr parseParenthesis(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.consume();
        Expr expr = Expr.parse(parser);
        parser.expect(TokenType.CLOSE_PARENTHESIS, String.format("Símbolo inesperado, esperávamos ')', mas recebemos '%s'", parser.peekValue()));
        return expr;
    }
}
