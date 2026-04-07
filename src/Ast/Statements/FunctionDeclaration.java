package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Ast.Types.Statement;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidNodeException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
import Parser.Parser;

import java.util.ArrayList;

public class FunctionDeclaration extends Statement
{
    public String identifier;
    public ArrayList<String> parameters;
    public ArrayList<Statement> body;

    protected FunctionDeclaration(
        String identifier,
        ArrayList<String> parameters,
        ArrayList<Statement> body)
    {
        super(NodeType.FunctionDeclaration);
        this.identifier = identifier;
        this.parameters = parameters;
        this.body = body;
    }

    public static Statement parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.consume();
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Expecting identifier name following function.");
        String name = identifierToken.value;

        ArrayList<Expr> parametersIdentifiers = CallExpr.parseArgs(parser);
        ArrayList<String> parameters = new ArrayList<>();

        for (Expr identifier : parametersIdentifiers)
        {
            if (identifier.type != NodeType.Identifier)
            {
                throw new InvalidNodeException("Expected parameter identifier in parameters list.");
            }

            parameters.add(((Identifier) identifier).get());
        }

        parser.expect(TokenType.OPEN_BRACE, "Expecting '{' after function arguments declaration.");
        ArrayList<Statement> body = new ArrayList<>();

        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE)) {
            body.add(Statement.parse(parser));
        }

        parser.expect(TokenType.CLOSE_BRACE, "Expecting '}' after function body declaration.");

        return FunctionDeclaration.create(name, parameters, body);
    }

    public static FunctionDeclaration create(
        String identifier,
        ArrayList<String> parameters,
        ArrayList<Statement> body)
    {
        return new FunctionDeclaration(identifier, parameters, body);
    }


    // TODO: refactor these two functions below
    private String printParams(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[\n");
        for (String parameter : parameters)
        {
            ret.repeat("\t", next)
                    .append(parameter)
                    .append(',')
                    .append('\n');
        }
        return ret.repeat("\t", level)
                .append("]")
                .toString();
    }

    private String printBody(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[");
        for (Statement statement : body)
        {
            ret.repeat("\t", next)
                    .append(statement.print(next))
                    .append(',');
        }
        return ret.append("\n")
                .repeat("\t", level)
                .append("]")
                .toString();
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "parameters: " + printParams(next) + ",\n" +
                "\t".repeat(next) + "body: " + printBody(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
