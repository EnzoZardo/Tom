package Ast.Expressions.Literals;

import Ast.Expressions.MemberExpr;
import Entities.Enums.Ast.NodeType;
import Ast.Expressions.BinaryExpr;
import Ast.Expressions.Property;
import Entities.Abstractions.Ast.Expr;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
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
            Token key = parser.expect(TokenType.IDENTIFIER, "Esperávamos um identificador para uma " +
                    "chave do objeto.");

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

            parser.expect(TokenType.COLON, "Esperávamos ':' para marcar " +
                    "o valor da chave " + key + ", mas recebemos '%s'");
            Expr value = Expr.parse(parser);

            if (!parser.peekIs(TokenType.CLOSE_BRACE))
            {
                parser.expect(TokenType.COMMA, "Esperávamos ',' ou '}' " +
                        "para a chave " + key + ", mas recebemos '%s'");
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }

            properties.add(Property.create(key.value, value));
        }

        parser.expect(TokenType.CLOSE_BRACE, "Esperávamos '}' para fechar o objeto, mas recebemos '%s'");

        return ObjectLiteral.create(properties);
    }

    public static ObjectLiteral create()
    {
        return new ObjectLiteral(new ArrayList<>());
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
            .repeat("\t", level)
            .append('[');

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
