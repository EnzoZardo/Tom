package Ast.Expressions;

import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class MemberExpr extends Expr
{
    public Expr object;
    public Expr property;
    public boolean computed;

    protected MemberExpr(
        Expr object,
        Expr property,
        boolean computed)
    {
        super(NodeType.MemberExpression);
        this.object = object;
        this.property = property;
        this.computed = computed;
    }

    public static MemberExpr create(
        Expr object,
        Expr property,
        boolean computed)
    {
        return new MemberExpr(object, property, computed);
    }

    public static ErrorOr<Expr> parse(Parser parser)
    {
        ErrorOr<Expr> objectOr = PrimaryExpr.parse(parser);

        if (objectOr.isError()) return objectOr.propagateError();
        Expr object = objectOr.value;

        while (true)
        {
            if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
            {
                ErrorOr<Expr> callOr = CallExpr.parse(parser, object);
                if (callOr.isError()) return callOr.propagateError();

                object = callOr.value;
            }
            else if (parser.peekIs(TokenType.DOT))
            {
                parser.consume();

                ErrorOr<Expr> propertyOr = PrimaryExpr.parse(parser);
                if (propertyOr.isError()) return propertyOr.propagateError();
                Expr property = propertyOr.value;

                if (property.type != NodeType.Identifier)
                    return ErrorOr.Fail(
                        "Esperávamos um nome para a chave de nosso objeto após um ponto - .",
                        ErrorType.ParsingError);

                object = MemberExpr.create(object, property, false);
            }
            else if (parser.peekIs(TokenType.OPEN_BRACKETS))
            {
                parser.consume();

                ErrorOr<Expr> propertyOr = Expr.parse(parser);
                if (propertyOr.isError()) return propertyOr.propagateError();

                ErrorOr<Token> closeOr = parser.expect(
                    TokenType.CLOSE_BRACKETS,
                    "Esperávamos um fechamento de colchetes - ] - após acesso a uma computado de um objeto"
                );
                if (closeOr.isError()) return closeOr.propagateError();

                object = MemberExpr.create(object, propertyOr.value, true);
            }
            else
            {
                break;
            }
        }

        return ErrorOr.Success(object);
    }

    public static ErrorOr<Expr> parseCall(Parser parser)
    {
        ErrorOr<Expr> memberOr = MemberExpr.parse(parser);
        if (memberOr.isError()) return memberOr.propagateError();

        if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
            return CallExpr.parse(parser, memberOr.value);

        return memberOr;
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "object: " + object.print(next) + ",\n" +
                "\t".repeat(next) + "property: " + property.print(next) + ",\n" +
                "\t".repeat(next) + "computed: " + computed + ",\n" +
                "\t".repeat(level) + "}";
    }
}