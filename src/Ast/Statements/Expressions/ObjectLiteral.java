package Ast.Statements.Expressions;

import Ast.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
import Parser.Parser;

import java.util.ArrayList;

public class ObjectLiteral extends Expr
{
    public ArrayList<Property> properties;

    protected ObjectLiteral(ArrayList<Property> properties)
    {
        super(NodeType.ObjectLiteral);
        this.properties = properties;
    }

    public static ObjectLiteral create(ArrayList<Property> properties)
    {
        return new ObjectLiteral(properties);
    }

    public static Expr parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        if (!parser.peekIs(TokenType.OPEN_BRACE))
        {
            return BinaryExpr.parseAdditive(parser);
        }
        parser.consume();

        ArrayList<Property> properties = new ArrayList<>();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            Token key = parser.expect(TokenType.IDENTIFIER, "Expecting identifier as object key.");

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
                properties.add(Property.create(key.value));
                continue;
            }

            if (parser.peekIs(TokenType.CLOSE_BRACE))
            {
                properties.add(Property.create(key.value));
                continue;
            }

            parser.expect(TokenType.COLON, "Expecting colon after object key.");
            Expr value = Expr.parse(parser);

            if (!parser.peekIs(TokenType.CLOSE_BRACE))
            {
                parser.expect(TokenType.COMMA, "Invalid token found parsing object. Expected comma or close brace.");
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }

            properties.add(Property.create(key.value, value));
        }

        parser.expect(TokenType.CLOSE_BRACE, "Expecting a close brace after last object value.");
        return ObjectLiteral.create(properties);
    }

    public static ObjectLiteral create()
    {
        return new ObjectLiteral(new ArrayList<>());
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[");
        for (Expr entry : properties)
        {
            ret.repeat("\t", next)
                    .append(entry.print(next))
                    .append(',');
        }
        return ret.append("\n")
                .repeat("\t", level)
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "properties: " + printProps(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
