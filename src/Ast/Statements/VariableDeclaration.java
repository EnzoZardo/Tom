package Ast.Statements;

import Ast.Types.Enums.NodeType;

public class VariableDeclaration extends Expr
{
    public Expr value;
    public boolean constant;
    public String identifier;

    protected VariableDeclaration(
            Expr value,
            String identifier,
            boolean constant)
    {
        super(NodeType.VariableDeclaration);
        this.value = value;
        this.identifier = identifier;
        this.constant = constant;
    }

    public static VariableDeclaration create(
            Expr value,
            String identifier,
            boolean constant)
    {
        return new VariableDeclaration(value, identifier, constant);
    }

    public static VariableDeclaration notInstanced(
            String identifier)
    {
        return new VariableDeclaration(null, identifier, false);
    }

    @Override
    public String print(int level)
    {
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(level + 1) + "node: " + type.toString() + ",\n" +
                "\t".repeat(level + 1) + "value: " + value.print(level + 1) + ",\n" +
                "\t".repeat(level + 1) + "constant: " + constant + ",\n" +
                "\t".repeat(level + 1) + "identifier: " + identifier + ",\n" +
                "\t".repeat(level) + "}";
    }
}
