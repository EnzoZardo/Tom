package Ast.Types;

import Entities.Common.Result.ResultVoid;
import Entities.Enums.TypeKind;
import Entities.Abstractions.Type;
import Entities.Enums.Lexer.TokenType;
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

    public static ResultVoid equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.FunctionType) {
            return ObjectType.equals(type1, type2);
        }

        FunctionType function1 = (FunctionType) type1;
        FunctionType function2 = (FunctionType) type2;

        if (function1.parameters.size() != function2.parameters.size())
        {
            return ResultVoid.Fail("A quantidade dos parâmetros da função com seu tipo diferem.");
        }

        if (!Type.equals(function1.returnType, function2.returnType))
        {
            return ResultVoid.Fail("A o tipo de retorno da função com seu tipo diferem.");
        }

        for (int i = 0; i < function1.parameters.size(); i++)
        {
            ResultVoid result = Type.equals(function1.parameters.get(i), function2.parameters.get(i));
            if (result.isFailure())
            {
                return result;
            }
        }

        return ResultVoid.Ok();
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
                parser.expect(TokenType.COMMA, "Símbolo inválido %s na declaração de uma função. Esperávamos ':' ou ')'");
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }

            parameters.add(type);
        }

        parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' depois da declaração dos tipos dos " +
                "argumentos da função.");
        parser.expect(TokenType.COLON, "Esperávamos ':' para declarar o tipo de retorno da função.");
        return FunctionType.create(parameters, Type.parse(parser));
    }

    private String printProps(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
            .repeat("\t", level)
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
        return "\n" +
            "\t".repeat(level) + "{\n" +
            "\t".repeat(next) + "node: " + type + ",\n" +
            "\t".repeat(next) + "properties: " + printProps(next) + ",\n" +
            "\t".repeat(level) + "}";
    }
}
