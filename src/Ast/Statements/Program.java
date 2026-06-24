package Ast.Statements;

import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Statement;
import Lexer.Lexer;
import Lexer.Tokens.Token;
import Parser.Parser;

import java.util.ArrayList;

public class Program extends Statement
{
    public final ArrayList<Statement> body;

    protected Program()
    {
        super(NodeType.Program);
        body = new ArrayList<>();
    }

    public static ErrorOr<Program> initialize(String content, String file)
    {
        Lexer lexer = Lexer.create(content.toCharArray(), file);
        ErrorOr<ArrayList<Token>> tokenization = lexer.tokenize();

        if (tokenization.isError()) return tokenization.propagateError();

        Parser parser = Parser.create(tokenization.value);
        ErrorOr<Program> build = parser.build();

        if (build.isError()) return build.propagateError();

        return build;
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
            "\tbody: [" + print(2) + "\n" + "\t]\n" +
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
