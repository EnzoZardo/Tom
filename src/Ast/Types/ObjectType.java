package Ast.Types;

import Entities.Common.Result.ErrorOr;
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

    public static ErrorOr<Void> equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.ObjectType) {
            return ArrayType.equals(type1, type2);
        }

        ObjectType object1 = (ObjectType) type1;
        ObjectType object2 = (ObjectType) type2;

        if (object1.properties.size() != object2.properties.size())
        {
            return ErrorOr.Fail("A quantidade de propriedades do objeto difere da quantidade de " +
                    "propriedades de seu tipo");
        }

        for (int i = 0; i < object1.properties.size(); i++)
        {
            ObjectTypeProperty prop1 = object1.properties.get(i);
            ObjectTypeProperty prop2 = object2.properties.get(i);

            if (!prop1.key.equals(prop2.key)) {
                return ErrorOr.Fail("O nome das chaves do objeto é diferente.");
            }

            if (Type.equals(prop1.type, prop2.type).isError()) {
                return ErrorOr.Fail("O tipo das chaves do objeto é diferente.");
            }
        }

        return ErrorOr.Success();
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

    public static ErrorOr<Type> parse(Parser parser)
    {
        if (!parser.peekIs(TokenType.OPEN_BRACE))
        {
            return ArrayType.parse(parser);
        }

        parser.consume();

        ArrayList<ObjectTypeProperty> properties = new ArrayList<>();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            ErrorOr<Token> keyOr = parser.expect(TokenType.IDENTIFIER, "Expecting identifier as object-type key.");
            if (keyOr.isError()) return keyOr.propagateError();
            ErrorOr<Token> colonOr = parser.expect(TokenType.COLON, "Expecting colon after object-type key.");
            if (colonOr.isError()) return colonOr.propagateError();

            ErrorOr<Type> valueOr = Type.parse(parser);
            if (valueOr.isError()) return valueOr.propagateError();
            Type value = valueOr.value;

            if (!parser.peekIs(TokenType.CLOSE_BRACE))
            {
                ErrorOr<Token> semiOr = parser.expect(TokenType.SEMICOLON, "Invalid token found parsing object-like type. Expected semicolon or close brace.");
                if (semiOr.isError()) return semiOr.propagateError();
            }

            if (parser.peekIs(TokenType.SEMICOLON))
            {
                parser.consume();
            }

            properties.add(ObjectTypeProperty.create(keyOr.value.value, value));
        }

        ErrorOr<Token> closeOr = parser.expect(TokenType.CLOSE_BRACE, "Expecting a close brace after last object value.");
        if (closeOr.isError()) return closeOr.propagateError();
        properties.sort(Comparator.comparing(p -> p.key));
        return ErrorOr.Success(ObjectType.create(properties));
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

    @Override
    public String toString()
    {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < properties.size(); i++)
        {
            params.append(properties.get(i));
            if (i < properties.size() - 1) {
                params.append(", ");
            }
        }
        return "{ " +  params + " }";
    }
}