package Ast.Statements.Types;

import Ast.Enums.TypeKind;
import Lexer.Types.Enums.TokenType;
import Parser.Parser;
import Runtime.Environment;

import java.util.ArrayList;

public class FunctionType extends Type
{
    public ArrayList<Type> parameters;
    public Type returnType;

    protected FunctionType(ArrayList<Type> arguments, Type returnType)
    {
        super(TypeKind.FunctionType);
        this.parameters = arguments;
        this.returnType = returnType;
    }

    public static FunctionType create(ArrayList<Type> arguments, Type returnType)
    {
        return new FunctionType(arguments, returnType);
    }

    public static boolean equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.FunctionType) {
            return ObjectType.equals(type1, type2);
        }

        FunctionType function1 = (FunctionType) type1;
        FunctionType function2 = (FunctionType) type2;

        if (function1.parameters.size() != function2.parameters.size())
        {
            return false;
        }

        if (!Type.equals(function1.returnType, function2.returnType))
        {
            return false;
        }

        for (int i = 0; i < function1.parameters.size(); i++)
        {
            if (!Type.equals(function1.parameters.get(i), function2.parameters.get(i))) {
                return false;
            }
        }

        return true;
    }

    public static Type reduce(Environment env, Type type)
    {
        if (type.type != TypeKind.FunctionType)
        {
            return ObjectType.reduce(env, type);
        }

        FunctionType functionType = (FunctionType) type;
        ArrayList<Type> params = new ArrayList<>();

        for (Type param : functionType.parameters)
        {
            params.add(Type.reduce(env, param));
        }

        return FunctionType.create(params, Type.reduce(env, functionType.returnType));
    }

    public static Type parse(Parser parser)
    {
        if (!parser.peekIs(TokenType.OPEN_PARENTHESIS))
        {
            return ObjectType.parse(parser);
        }

        parser.consume();

        ArrayList<Type> parameters = new ArrayList<>();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            Type type = Type.parse(parser);

            if (!parser.peekIs(TokenType.CLOSE_PARENTHESIS))
            {
                parser.expect(TokenType.COMMA, "Invalid token found parsing function type. Expected COMMA or close parenthesis.");
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }

            parameters.add(type);
        }

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Expecting a close parenthesis after last object value.");
        parser.expect(TokenType.COLON, "Invalid token found parsing function type. Expected colon or close parenthesis.");

        Type returnType = Type.parse(parser);

        return FunctionType.create(parameters, returnType);
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[");
        for (Type entry : parameters)
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
