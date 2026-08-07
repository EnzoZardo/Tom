package Ast.Types;

import Entities.Abstractions.Type;
import Entities.Enums.TypeKind;
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

    public static Type reduce(Environment env, Type type) {
        if (type.type != TypeKind.ClassType)
            return type;

        ClassType classType = (ClassType) type;

        Type resolved = env.lookupType(classType.name);
        ArrayList<String> typeParams = env.lookupTypeParameters(classType.name);

        if (!typeParams.isEmpty() && !classType.parameters.isEmpty())
            return reduceParameters(env, typeParams, classType, resolved);

        return type;
    }

    private static Type reduceParameters(Environment env, ArrayList<String> typeParams, ClassType symbolType, Type resolved)
    {
        HashMap<String, Type> mapping = new HashMap<>();
        for (int i = 0; i < typeParams.size() && i < symbolType.parameters.size(); i++)
        {
            mapping.put(typeParams.get(i), symbolType.parameters.get(i));
        }
        resolved = SymbolType.substitute(resolved, mapping);
        return Type.reduce(env, resolved);
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
        return name;
    }
}