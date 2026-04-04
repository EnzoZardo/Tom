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
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "left: " + left.print(next) + ",\n" +
                "\t".repeat(next) + "right: " + right.print(next) + ",\n" +
                "\t".repeat(next) + "operator: " + operator + ",\n" +
                "\t".repeat(level) + "}";
    }
}
