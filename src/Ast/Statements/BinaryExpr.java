package Ast.Statements;

import Ast.Types.Enums.NodeType;

public class BinaryExpr extends Expr
{
    public Expr left;
    public Expr right;
    public String operator;

    protected BinaryExpr(
        Expr left,
        Expr right,
        String operator)
    {
        super(NodeType.BinaryExpr);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public static BinaryExpr create(
        Expr left,
        Expr right,
        String operator)
    {
        return new BinaryExpr(left, right, operator);
    }

    @Override
    public String print(int level) {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "left: " + left.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "right: " + right.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "operator: " + operator + ",\n" +
                "\t".repeat(level) + "}";
    }
}
