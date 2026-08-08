package Ast.Types;

import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.TypeKind;
import Entities.Exceptions.Parser.ParsingException;
import Runtime.Environment;

import java.util.ArrayList;
import java.util.HashMap;

import Ast.Types.SymbolType;

public class ClassType extends Type
{
    public final String name;
    public final ClassType parent;
    public final ArrayList<Type> parameters;

    private ClassType(String name, ClassType parent, ArrayList<Type> parameters)
    {
        super(TypeKind.ClassType);
        this.name = name;
        this.parent = parent;
        this.parameters = parameters;
    }

    public static ClassType create(String name, ClassType parent, ArrayList<Type> parameters)
    {
        return new ClassType(name, parent, parameters);
    }

    public static ClassType create(String name)
    {
        return new ClassType(name, null, new ArrayList<>());
    }

    public static Type reduce(Environment env, Type type)
    {
        if (type.type != TypeKind.ClassType)
            return type;

        ClassType classType = (ClassType) type;

        ArrayList<String> typeParams = env.lookupTypeParameters(classType.name);

        if (!typeParams.isEmpty())
        {
            if (classType.parameters.size() != typeParams.size())
            {
                throw new ParsingException(String.format(
                    "O tipo %s esperava %d argumento(s) de tipo, mas recebeu %d.",
                    classType.name,
                    typeParams.size(),
                    classType.parameters.size()));
            }

            HashMap<String, Type> mapping = new HashMap<>();
            for (int i = 0; i < typeParams.size(); i++)
            {
                mapping.put(typeParams.get(i), classType.parameters.get(i));
            }

            ArrayList<Type> newParams = new ArrayList<>();
            for (Type param : classType.parameters)
            {
                newParams.add(Type.reduce(env, SymbolType.substitute(param, mapping)));
            }
            return ClassType.create(classType.name, classType.parent, newParams);
        }

        return classType;
    }

    public static ErrorOr<Void> equals(Type type1, Type type2)
    {
        if (type1.type != TypeKind.ClassType)
        {
            return GenericType.equals(type1, type2);
        }

        ClassType class1 = (ClassType) type1;
        ClassType class2 = (ClassType) type2;

        if (!class1.name.equals(class2.name))
        {
            return ErrorOr.Fail("As classes " + class1.name + " e " + class2.name + " diferem.");
        }

        if (class1.parameters.size() != class2.parameters.size())
        {
            return ErrorOr.Fail("A quantidade de argumentos de tipo da classe " + class1.name + " difere.");
        }

        for (int i = 0; i < class1.parameters.size(); i++)
        {
            ErrorOr<Void> result = Type.equals(class1.parameters.get(i), class2.parameters.get(i));
            if (result.isError()) return result;
        }

        return ErrorOr.Success();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;

        if (parent == null) {
            return "\n" + "\t".repeat(level) + "{\n" +
                    "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                    "\t".repeat(next) + "name: " + name + ",\n" +
                    "\t".repeat(level) + "}";
        }

        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "name: " + name + ",\n" +
                "\t".repeat(next) + "parent: " + parent.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }

    @Override
    public String toString()
    {
        if (parameters.isEmpty())
            return name;

        StringBuilder builder = new StringBuilder(name).append("<");
        for (int i = 0; i < parameters.size(); i++)
        {
            if (i > 0) builder.append(", ");
            builder.append(parameters.get(i));
        }
        return builder.append(">").toString();
    }
}
