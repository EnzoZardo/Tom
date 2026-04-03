package Ast.Statements;

import Ast.Types.Enums.NodeType;

public class Property extends Expr
{
    public String key;
    public Expr value;

    protected Property(String key, Expr value)
    {
        super(NodeType.Property);
        this.key = key;
        this.value = value;
    }

    public static Property create(String key, Expr value)
    {
        return new Property(key, value);
    }

    public static Property create(String key)
    {
        return new Property(key, null);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "key: " + key + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
