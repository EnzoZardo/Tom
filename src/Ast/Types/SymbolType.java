package Ast.Types;

import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Constants.ReservedKeys;
import Entities.Constants.ReservedPrimitiveTypes;
import Entities.Enums.TypeKind;
import Entities.Abstractions.Type;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.Parser.ParsingException;
import Lexer.Tokens.Token;
import Parser.Parser;
import Runtime.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SymbolType extends Type
{
    public String value;
    public ArrayList<Type> parameters;

    protected SymbolType(String value, ArrayList<Type> parameters)
    {
        super(TypeKind.SymbolType);
        this.value = value;
        this.parameters = parameters;
    }

    protected SymbolType(String value)
    {
        super(TypeKind.SymbolType);
        this.value = value;
        this.parameters = new ArrayList<>();
    }

    public static SymbolType create(String value, ArrayList<Type> parameters)
    {
        return new SymbolType(value, parameters);
    }

    public static SymbolType create(String value)
    {
        return new SymbolType(value);
    }

    private static final ThreadLocal<Set<String>> resolving = ThreadLocal.withInitial(HashSet::new);

    public static Type reduce(Environment env, Type type)
    {
        if (type.type == TypeKind.NeverType)
            return type;

        SymbolType symbolType = (SymbolType) type;

        if (ReservedPrimitiveTypes.isReserved(symbolType.value))
        {
            if (!symbolType.parameters.isEmpty())
            {
                throw new ParsingException("O tipo primitivo " + symbolType.value + " não aceita argumentos de tipo.");
            }
            return symbolType;
        }

        Type resolved = env.lookupType(symbolType.value);
        ArrayList<String> typeParams = env.lookupTypeParameters(symbolType.value);

        if (!typeParams.isEmpty() || !symbolType.parameters.isEmpty())
        {
            return reduceParameters(env, typeParams, symbolType, resolved);
        }

        return resolved;
    }

    private static Type reduceParameters(Environment env, ArrayList<String> typeParams, SymbolType symbolType, Type resolved)
    {
        if (symbolType.parameters.size() != typeParams.size())
        {
            throw new ParsingException(String.format(
                "O tipo %s esperava %d argumento(s) de tipo, mas recebeu %d.",
                symbolType.value,
                typeParams.size(),
                symbolType.parameters.size()));
        }

        HashMap<String, Type> mapping = new HashMap<>();
        for (int i = 0; i < typeParams.size(); i++)
        {
            mapping.put(typeParams.get(i), symbolType.parameters.get(i));
        }

        if (resolved.type == TypeKind.ClassType)
        {
            ClassType clazz = (ClassType) resolved;
            ArrayList<Type> newParams = new ArrayList<>();
            for (Type param : symbolType.parameters)
                newParams.add(substitute(param, mapping));
            return Type.reduce(env, ClassType.create(clazz.name, clazz.parent, newParams));
        }

        String key = symbolType.value + ":" + symbolType.parameters;
        if (!resolving.get().add(key))
        {
            throw new ParsingException("Detectamos recursão infinita na definição do tipo genérico " + symbolType.value + ".");
        }

        try
        {
            resolved = substitute(resolved, mapping);
            return Type.reduce(env, resolved);
        }
        finally
        {
            resolving.get().remove(key);
        }
    }

    public static Type substitute(Type type, HashMap<String, Type> mapping)
    {
        switch (type.type)
        {
            case SymbolType ->
            {
                SymbolType symbol = (SymbolType) type;
                if (mapping.containsKey(symbol.value) && symbol.parameters.isEmpty())
                    return mapping.get(symbol.value);
                if (!symbol.parameters.isEmpty())
                {
                    ArrayList<Type> newParams = new ArrayList<>();
                    for (Type param : symbol.parameters)
                        newParams.add(substitute(param, mapping));
                    return SymbolType.create(symbol.value, newParams);
                }
                return symbol;
            }
            case ObjectType ->
            {
                ObjectType obj = (ObjectType) type;
                ArrayList<ObjectTypeProperty> newProps = new ArrayList<>();
                for (ObjectTypeProperty prop : obj.properties)
                    newProps.add(ObjectTypeProperty.create(prop.key, substitute(prop.type, mapping)));
                return ObjectType.create(newProps);
            }
            case ArrayType ->
            {
                ArrayType arr = (ArrayType) type;
                return ArrayType.create(substitute(arr.underlying, mapping));
            }
            case FunctionType ->
            {
                FunctionType func = (FunctionType) type;
                ArrayList<Type> newParams = new ArrayList<>();
                for (Type p : func.parameters)
                    newParams.add(substitute(p, mapping));
                return FunctionType.create(newParams, substitute(func.returnType, mapping));
            }
            case BinaryType ->
            {
                BinaryType bin = (BinaryType) type;
                return BinaryType.create(substitute(bin.left, mapping), substitute(bin.right, mapping));
            }
            case GenericType ->
            {
                GenericType generic = (GenericType) type;
                if (mapping.containsKey(generic.name))
                    return mapping.get(generic.name);
                return generic;
            }
            case ClassType ->
            {
                ClassType clazz = (ClassType) type;
                ArrayList<Type> newParams = new ArrayList<>();
                for (Type param : clazz.parameters)
                    newParams.add(substitute(param, mapping));
                return ClassType.create(clazz.name, clazz.parent, newParams);
            }
            default -> { return type; }
        }
    }

    public static Type parse(Parser parser)
    {
        if (parser.peekIs(TokenType.OPEN_PARENTHESIS))
        {
            parser.consume();
            Type currType = Type.parse(parser);
            parser.expect(TokenType.CLOSE_PARENTHESIS, "Esperávamos um fechamento de parênteses na declaração " +
                    "do tipo " + currType);
            return currType;
        }

        Token token = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do tipo enquanto analisávamos.");

        if (parser.peekIs(TokenType.BINARY_OPERATOR) && ReservedKeys.Minor.equals(parser.peekValue()))
            return SymbolType.create(token.value, parseArgs(parser));

        return SymbolType.create(token.value);
    }

    public static ArrayList<Type> parseArgumentsList(Parser parser)
    {
        ArrayList<Type> args = new ArrayList<>();

        Type first = Type.parse(parser);

        args.add(first);

        while (parser.notEof() && parser.peekIs(TokenType.COMMA))
        {
            parser.consume();

            Type arg = Type.parse(parser);

            args.add(arg);
        }

        return args;
    }

    public static ArrayList<Type> parseArgs(Parser parser)
    {
        String message = "Esperávamos um 'menor que' - < - para " +
                "abrir a lista de argumentos de um tipo, mas recebemos outro símbolo no código - %s";
        Token open = parser.expect(TokenType.BINARY_OPERATOR, message);

        if (!ReservedKeys.Minor.equals(open.value))
            throw new ParsingException(String.format(message, open.value), ErrorType.ParsingError, open.location);

        if (parser.peekIs(TokenType.CLOSE_PARENTHESIS))
        {
            parser.consume();
            return new ArrayList<>();
        }

        ArrayList<Type> args = SymbolType.parseArgumentsList(parser);

        message = "Esperávamos um 'maior que' " +
                " - > - para fechar a lista de argumentos de uma função, mas recebemos outro " +
                "símbolo no código - %s";

        Token close = parser.expect(TokenType.BINARY_OPERATOR, message);

        if (!ReservedKeys.Greater.equals(close.value))
            throw new ParsingException(String.format(message, close.value), ErrorType.ParsingError, open.location);

        return args;
    }

    public static ErrorOr<Void> equals(Type type1, Type type2)
    {
        SymbolType symbol1 = (SymbolType) type1;
        SymbolType symbol2 = (SymbolType) type2;
        if (!symbol1.value.equals(symbol2.value))
        {
            return ErrorOr.Fail("Os símbolos dos tipos diferem.");
        }

        if (symbol1.parameters.size() != symbol2.parameters.size())
        {
            return ErrorOr.Fail("A quantidade de argumentos de tipo do símbolo " + symbol1.value + " difere.");
        }

        for (int i = 0; i < symbol1.parameters.size(); i++)
        {
            ErrorOr<Void> result = Type.equals(symbol1.parameters.get(i), symbol2.parameters.get(i));
            if (result.isError()) return result;
        }

        return ErrorOr.Success();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "symbol: " + value + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        return value;
    }
}
