package Ast.Expressions.Literals;

import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Ast.Expressions.Property;
import Entities.Abstractions.Ast.Expr;
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

    public static ErrorOr<Expr> parse(Parser parser)
    {
        parser.consume();

        ArrayList<Property> properties = new ArrayList<>();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            ErrorOr<Token> keyOr = parser.expect(TokenType.IDENTIFIER, "Esperávamos um nome para a " +
                "chave do nosso objeto, mas recebemos outro símbolo no código - %s");
            if (keyOr.isError()) return keyOr.propagateError();
            String key = keyOr.value.value;

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
                properties.add(Property.create(key));
                continue;
            }

            if (parser.peekIs(TokenType.CLOSE_BRACE))
            {
                properties.add(Property.create(key));
                continue;
            }

            ErrorOr<Token> colonOr = parser.expect(TokenType.COLON, "Esperávamos dois pontos - : - para marcar " +
                "o valor da chave " + key + ", mas recebemos outro símbolo no código - %s");
            if (colonOr.isError()) return colonOr.propagateError();

            ErrorOr<Expr> valueOr = Expr.parse(parser);
            if (valueOr.isError()) return valueOr.propagateError();

            if (!parser.peekIs(TokenType.CLOSE_BRACE))
            {
                ErrorOr<Token> commaOr = parser.expect(TokenType.COMMA, "Esperávamos uma vírgula - , - ou um" +
                    " fechamento de chaves - } - para a chave " + key + ", mas recebemos outro símbolo no código" +
                    " - %s");
                if (commaOr.isError()) return commaOr.propagateError();
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }

            properties.add(Property.create(key, valueOr.value));
        }

        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_BRACE, "Esperávamos '}' para fechar o objeto, mas recebemos '%s'");
        if (closeOr.isError()) return closeOr.propagateError();

        return ErrorOr.Success(ObjectLiteral.create(properties));
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
