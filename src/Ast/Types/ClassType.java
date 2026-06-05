package Ast.Types;

import Entities.Abstractions.Type;
import Entities.Enums.TypeKind;

public class ClassType extends Type
{
    public final String name;
    public final ClassType parent;

    private ClassType(String name, ClassType parent)
    {
        super(TypeKind.ClassType);
        this.name = name;
        this.parent = parent;
    }

    public static ClassType create(String name, ClassType parent)
    {
        return new ClassType(name, parent);
    }

    public static ClassType create(String name)
    {
        return new ClassType(name, null);
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