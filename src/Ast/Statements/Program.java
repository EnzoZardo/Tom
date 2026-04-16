package Ast.Statements;

import Ast.Enums.NodeType;

import java.util.ArrayList;

public class Program extends Statement
{
    public final ArrayList<Statement> body;

    protected Program()
    {
        super(NodeType.Program);
        body = new ArrayList<>();
    }

    public static Program create()
    {
        return new Program();
    }

    public void addStatement(Statement stmt)
    {
        body.add(stmt);
    }

    @Override
    public String toString() {
        return "{\n" +
                "\tnode: " + type.toString() + "\n" +
                "\tbody: [" +
                    print(2) + "\n" +
                "\t]\n" +
                "}";
    }

    @Override
    public String print(int level)
    {
        StringBuilder stmts = new StringBuilder();
        for (int i = 0; i < body.size(); i++)
        {
            stmts.append(body.get(i).print(2));
            if (i < body.size() - 1)
            {
                stmts.append(',');
            }
        }
        return stmts.toString();
    }
}
