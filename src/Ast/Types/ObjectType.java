package Ast.Types;

import Entities.Common.Result.ResultVoid;
import Entities.Enums.TypeKind;
import Entities.Abstractions.Type;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;
import Runtime.Environment;

import java.util.ArrayList;
import java.util.Comparator;

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

    public static ResultVoid equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.ObjectType) {
            return ArrayType.equals(type1, type2);
        }

        ObjectType object1 = (ObjectType) type1;
        ObjectType object2 = (ObjectType) type2;

        if (object1.properties.size() != object2.properties.size())
        {
            return ResultVoid.Fail("A quantidade de propriedades do objeto difere da quantidade de " +
                    "propriedades de seu tipo");
        }

        for (int i = 0; i < object1.properties.size(); i++)
        {
            ObjectTypeProperty prop1 = object1.properties.get(i);
            ObjectTypeProperty prop2 = object2.properties.get(i);

            if (!prop1.key.equals(prop2.key)) {
                return ResultVoid.Fail("O nome das chaves do objeto é diferente.");
            }

            if (!Type.equals(prop1.type, prop2.type)) {
                return ResultVoid.Fail("O tipo das chaves do objeto é diferente.");
            }
        }

        return ResultVoid.Ok();
    }

    public static Type reduce(Environment env, Type type)
    {
        if (type.type != TypeKind.ObjectType)
        {
            return ArrayType.reduce(env, type);
        }

        ObjectType objectType = (ObjectType) type;
        ArrayList<ObjectTypeProperty> props = new ArrayList<>();

        for (ObjectTypeProperty prop : objectType.properties)
        {
            props.add(ObjectTypeProperty.create(prop.key, Type.reduce(env, prop.type)));
        }

        return ObjectType.create(props);
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

            //todo: check if already exists key and throw

            properties.add(ObjectTypeProperty.create(key.value, value));
        }

        parser.expect(TokenType.CLOSE_BRACE, "Expecting a close brace after last object value.");
        properties.sort(Comparator.comparing(p -> p.key));
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