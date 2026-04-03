package Ast.Statements;

import Ast.Types.Enums.NodeType;

public class MemberExpr extends Expr
{
    public Expr object;
    public Expr property;
    public boolean computed;

    protected MemberExpr(
            Expr object,
            Expr property,
            boolean computed)
    {
        super(NodeType.MemberExpression);
        this.object = object;
        this.property = property;
        this.computed = computed;
    }

    public static MemberExpr create(
            Expr object,
            Expr property,
            boolean computed)
    {
        return new MemberExpr(object, property, computed);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "object: " + object.print(next) + ",\n" +
                "\t".repeat(next) + "property: " + property.print(next) + ",\n" +
                "\t".repeat(next) + "computed: " + computed + ",\n" +
                "\t".repeat(level) + "}";
    }
}