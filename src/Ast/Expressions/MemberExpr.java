package Ast.Expressions;

import Entities.Common.Result.ErrorType;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.Parser.ParsingException;
import Entities.Enums.Lexer.TokenType;
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

    public static Expr parse(Parser parser)
    {
        Expr object = PrimaryExpr.parse(parser);

        while (true)
        {
            if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
            {
                object = CallExpr.parse(parser, object);
            }
            else if (parser.peekIs(TokenType.DOT))
            {
                parser.consume();

                Expr property = PrimaryExpr.parse(parser);

                if (property.type != NodeType.Identifier)
                    throw new ParsingException(
                        "Esperávamos um nome para a chave de nosso objeto após um ponto - .",
                        ErrorType.ParsingError);

                object = MemberExpr.create(object, property, false);
            }
            else if (parser.peekIs(TokenType.OPEN_BRACKETS))
            {
                parser.consume();

                Expr property = Expr.parse(parser);

                parser.expect(
                    TokenType.CLOSE_BRACKETS,
                    "Esperávamos um fechamento de colchetes - ] - após acesso a uma computado de um objeto"
                );

                object = MemberExpr.create(object, property, true);
            }
            else
            {
                break;
            }
        }

        return object;
    }

    public static Expr parseCall(Parser parser)
    {
        Expr member = MemberExpr.parse(parser);

        if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
            return CallExpr.parse(parser, member);

        return member;
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
