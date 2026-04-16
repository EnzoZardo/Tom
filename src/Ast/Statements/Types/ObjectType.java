package Ast.Statements.Types;

import Ast.Enums.TypeKind;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
import Parser.Parser;

import java.util.ArrayList;

public class ObjectType extends Type
{
    public ArrayList<ObjectTypeProperty> properties;

    protected ObjectType(ArrayList<ObjectTypeProperty> properties)
    {
        super(TypeKind.ObjectType);
        this.properties = properties;
    }

    public static ObjectType create(ArrayList<ObjectTypeProperty> properties)
    {
        return new ObjectType(properties);
    }

    public static Type parse(Parser parser)
    {
        if (!parser.peekIs(TokenType.OPEN_BRACE))
        {
            return ArrayType.parse(parser);
        }

        parser.consume();

        ArrayList<ObjectTypeProperty> properties = new ArrayList<>();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            Token key = parser.expect(TokenType.IDENTIFIER, "Expecting identifier as object-type key.");
            parser.expect(TokenType.COLON, "Expecting colon after object-type key.");

            Type value = Type.parse(parser);

            if (!parser.peekIs(TokenType.CLOSE_BRACE))
            {
                parser.expect(TokenType.SEMICOLON, "Invalid token found parsing object-like type. Expected semicolon or close brace.");
            }

            if (parser.peekIs(TokenType.SEMICOLON))
            {
                parser.consume();
            }

            properties.add(ObjectTypeProperty.create(key.value, value));
        }

        parser.expect(TokenType.CLOSE_BRACE, "Expecting a close brace after last object value.");
        return ObjectType.create(properties);
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[");
        for (Type entry : properties)
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
                "\t".repeat(next) + "node: " + type + ",\n" +
                "\t".repeat(next) + "properties: " + printProps(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}