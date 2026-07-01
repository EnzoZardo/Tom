package Ast.Expressions;

import Ast.Expressions.Literals.*;
import Entities.Abstractions.Ast.Expr;
import Entities.Common.Result.ErrorType;
import Entities.Exceptions.Parser.ParsingException;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class PrimaryExpr
{
    public static Expr parse(Parser parser)
    {
        return switch (parser.peekType())
        {
            case NEW -> ClassLiteral.parse(parser);
            case OPEN_BRACE -> ObjectLiteral.parse(parser);
            case OPEN_BRACKETS -> ArrayLiteral.parse(parser);
            case IDENTIFIER -> Identifier.create(parser.consume());
            case FLOAT_LITERAL -> FloatLiteral.create(parser.consume());
            case STRING_LITERAL -> StringLiteral.create(parser.consume());
            case OPEN_PARENTHESIS -> PrimaryExpr.parseParenthesis(parser);
            case INTEGER_LITERAL -> IntegerLiteral.create(parser.consume());
            default -> throw new ParsingException(String.format("Símbolo inesperado '%s'", parser.peekValue()));
        };
    }

    private static Expr parseParenthesis(Parser parser)
    {
        parser.consume();
        Expr expr = Expr.parse(parser);
        parser.expect(TokenType.CLOSE_PARENTHESIS,
                String.format("Símbolo inesperado, esperávamos ')', mas recebemos '%s'", parser.peekValue()));
        return expr;
    }
}
