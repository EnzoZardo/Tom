package Ast.Expressions;

import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Entities.Exceptions.Parser.InvalidTokenException;
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

    public static Expr parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        Expr object = PrimaryExpr.parse(parser);

        while (MemberExpr.isParsingMemberChaining(parser))
        {
            Token operator = parser.consume();
            boolean computed;
            Expr property;

            if (operator.type == TokenType.DOT)
            {
                computed = false;
                property = PrimaryExpr.parse(parser);

                if (property.type != NodeType.Identifier)
                {
                    throw new InvalidNodeException("Esperávamos um identificador depois de um '.'");
                }
            }
            else
            {
                computed = true;
                property = PrimaryExpr.parse(parser);
                parser.expect(TokenType.CLOSE_BRACKETS, "Esperávamos um ']' após uma expressão de membro personalizada.");
            }

            object = MemberExpr.create(object, property, computed);
        }

        return object;
    }

    public static Expr parseCall(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        Expr member = MemberExpr.parse(parser);

        if (parser.peekIs(TokenType.OPEN_PARENTHESIS)) {
            return CallExpr.parse(parser, member);
        }

        return member;
    }

    private static boolean isParsingMemberChaining(Parser parser)
    {
        return parser.peekIs(TokenType.DOT) || parser.peekIs(TokenType.OPEN_BRACKETS);
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