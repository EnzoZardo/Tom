package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidNodeException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
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

    public static Expr parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        Expr object = PrimaryExpr.parse(parser);

        while (MemberExpr.isParsingMemberChaining(parser))
        {
            Token operator = parser.consume();
            boolean computed;
            Expr property;

            if (operator.type == TokenType.DOT) {
                computed = false;
                property = PrimaryExpr.parse(parser);

                if (property.type != NodeType.Identifier)
                {
                    throw new InvalidNodeException("Expected an identifier after dot on member expression.");
                }
            }
            else
            {
                computed = true;
                property = PrimaryExpr.parse(parser);
                parser.expect(TokenType.CLOSE_BRACKETS, "Expecting close brackets after computed member expression.");
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

    public static MemberExpr create(
            Expr object,
            Expr property,
            boolean computed)
    {
        return new MemberExpr(object, property, computed);
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