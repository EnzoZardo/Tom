package Ast.Statements.Types;

import Ast.Enums.TypeKind;

public class ObjectTypeProperty extends Type
{
    public String key;
    public Type type;

    protected ObjectTypeProperty(String key, Type value)
    {
        super(TypeKind.ObjectTypeProperty);
        this.key = key;
        this.type = value;
    }

    public static ObjectTypeProperty create(String key, Type value)
    {
        return new ObjectTypeProperty(key, value);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.print(next) + ",\n" +
                "\t".repeat(next) + "key: " + key + ",\n" +
                "\t".repeat(next) + "value: " + type.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
