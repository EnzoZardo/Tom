package Ast.Types;

import Entities.Common.Result.ErrorOr;
import Entities.Enums.TypeKind;
import Entities.Abstractions.Type;
import Entities.Exceptions.Parser.ParsingException;
import Parser.Parser;
import Runtime.Environment;

public class GenericType extends Type
{
    public final String name;

    protected GenericType(String name)
    {
        super(TypeKind.GenericType);
        this.name = name;
    }

    public static GenericType create(String name)
    {
        return new GenericType(name);
    }

    public static Type reduce(Environment env, Type type)
    {
        return type;
    }

    public static ErrorOr<Void> equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.GenericType)
        {
            return SymbolType.equals(type1, type2);
        }

        GenericType generic1 = (GenericType) type1;
        GenericType generic2 = (GenericType) type2;

        if (generic1.name.equals(generic2.name))
            return ErrorOr.Success();

        return ErrorOr.Fail("Os parâmetros de tipo diferem.");
    }

    public static Type parse(Parser parser)
    {
        throw new ParsingException("Não é possível declarar esse tipo diretamente.");
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "name: " + name + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        return name;
    }
}
