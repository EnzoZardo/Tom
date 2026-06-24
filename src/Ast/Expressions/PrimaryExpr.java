package Ast.Expressions;

import Ast.Expressions.Literals.*;
import Entities.Abstractions.Ast.Expr;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class PrimaryExpr
{
    public static ErrorOr<Expr> parse(Parser parser)
    {
        return switch (parser.peekType())
        {
            case NEW -> ClassLiteral.parse(parser);
            case OPEN_BRACE -> ObjectLiteral.parse(parser);
            case OPEN_BRACKETS -> ArrayLiteral.parse(parser);
            case OPEN_PARENTHESIS -> PrimaryExpr.parseParenthesis(parser);
            case IDENTIFIER -> ErrorOr.Success(Identifier.create(parser.consume()));
            case FLOAT_LITERAL -> ErrorOr.Success(FloatLiteral.create(parser.consume()));
            case STRING_LITERAL -> ErrorOr.Success(StringLiteral.create(parser.consume()));
            case INTEGER_LITERAL -> ErrorOr.Success(IntegerLiteral.create(parser.consume()));
            default -> ErrorOr.Fail(
                String.format("Não esperávamos esse símbolo no código: '%s'", parser.peekValue()),
                ErrorType.ParsingError,
                parser.peek().location);
        };
    }

    private static ErrorOr<Expr> parseParenthesis(Parser parser)
    {
        parser.consume();
        ErrorOr<Expr> expr = Expr.parse(parser);
        if (expr.isError()) return expr.propagateError();
        ErrorOr<Token> close = parser.expect(TokenType.CLOSE_PARENTHESIS,
                String.format("Símbolo inesperado, esperávamos ')', mas recebemos '%s'", parser.peekValue()));
        if (close.isError()) return close.propagateError();
        return expr;
    }
}
