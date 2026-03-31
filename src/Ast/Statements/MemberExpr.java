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
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "object: " + object.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "property: " + property.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "computed: " + computed + ",\n" +
                "\t".repeat(level) + "}";
    }
}