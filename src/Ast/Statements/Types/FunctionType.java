package Ast.Statements.Types;

import Ast.Enums.TypeKind;
import Lexer.Types.Enums.TokenType;
import Parser.Parser;

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
