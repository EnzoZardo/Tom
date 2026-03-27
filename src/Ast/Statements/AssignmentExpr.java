package Ast.Statements;

import Ast.Types.Enums.NodeType;

public class AssignmentExpr extends Expr
{
    public Expr assigned;
    public Expr value;

    protected AssignmentExpr(
            Expr assigned,
            Expr right)
    {
        super(NodeType.AssignmentExpression);
        this.assigned = assigned;
        this.value = right;
    }

    public static AssignmentExpr create(
            Expr assigned,
            Expr value)
    {
        return new AssignmentExpr(assigned, value);
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "assigned: " + assigned.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "value: " + value.print(level + 1) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
