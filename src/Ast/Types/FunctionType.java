package Ast.Types;

import Entities.Common.Result.ErrorOr;
import Entities.Enums.TypeKind;
import Entities.Abstractions.Type;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
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

    public static ErrorOr<Void> equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.FunctionType) {
            return ObjectType.equals(type1, type2);
        }

        FunctionType function1 = (FunctionType) type1;
        FunctionType function2 = (FunctionType) type2;

        if (function1.parameters.size() != function2.parameters.size())
        {
            return ErrorOr.Fail("A quantidade dos parâmetros da função com seu tipo diferem.");
        }

        if (Type.equals(function1.returnType, function2.returnType).isError())
        {
            return ErrorOr.Fail("A o tipo de retorno da função com seu tipo diferem.");
        }

        for (int i = 0; i < function1.parameters.size(); i++)
        {
            ErrorOr<Void> result = Type.equals(function1.parameters.get(i), function2.parameters.get(i));
            if (result.isError())
            {
                return result;
            }
        }

        return ErrorOr.Success();
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

    public static ErrorOr<Type> parse(Parser parser)
    {
        if (!parser.peekIs(TokenType.OPEN_PARENTHESIS))
        {
            return ObjectType.parse(parser);
        }

        parser.consume();
        ArrayList<Type> parameters = new ArrayList<>();

        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            ErrorOr<Type> typeOr = Type.parse(parser);
            if (typeOr.isError()) return typeOr.propagateError();
            Type type = typeOr.value;

            if (!parser.peekIs(TokenType.CLOSE_PARENTHESIS))
            {
                ErrorOr<Token> commaOr = parser.expect(TokenType.COMMA, "Símbolo inválido %s na declaração de uma função. Esperávamos ':' ou ')'");
                if (commaOr.isError()) return commaOr.propagateError();
            }

            if (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
            }

            parameters.add(type);
        }

        ErrorOr<Token> closeParenOr = parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos ')' depois da declaração dos tipos dos " +
                "argumentos da função.");
        if (closeParenOr.isError()) return closeParenOr.propagateError();
        ErrorOr<Token> colonOr = parser.expect(TokenType.COLON, "Esperávamos ':' para declarar o tipo de retorno da função.");
        if (colonOr.isError()) return colonOr.propagateError();
        ErrorOr<Type> returnTypeOr = Type.parse(parser);
        if (returnTypeOr.isError()) return returnTypeOr.propagateError();
        return ErrorOr.Success(FunctionType.create(parameters, returnTypeOr.value));
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
            "\t".repeat(next) + "returnType: " + returnType.print(next) + ",\n" +
            "\t".repeat(next) + "parameters: " + printProps(next) + ",\n" +
            "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++)
        {
            params.append(parameters.get(i));
            if (i < parameters.size() - 1) {
                params.append(", ");
            }
        }
        return "(" +  params + "): " + returnType;
    }
}
