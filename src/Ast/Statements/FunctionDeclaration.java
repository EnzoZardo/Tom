package Ast.Statements;

import Ast.Statements.Expressions.CallExpr;
import Ast.Statements.Expressions.Expr;
import Ast.Statements.Expressions.Identifier;
import Ast.Enums.NodeType;
import Ast.Statements.Types.Type;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidNodeException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
import Parser.Parser;
import Types.ArgumentMetadata;
import Types.ExprMetadata;

import java.util.ArrayList;

public class FunctionDeclaration extends Statement
{
    public String identifier;
    public Type returnType;
    public ArrayList<ArgumentMetadata> parameters;
    public ArrayList<Statement> body;

    protected FunctionDeclaration(
        String identifier,
        ArrayList<ArgumentMetadata> parameters,
        ArrayList<Statement> body,
        Type returnType)
    {
        super(NodeType.FunctionDeclaration);
        this.identifier = identifier;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    public static Statement parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.consume();
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Expecting identifier name following function.");
        String name = identifierToken.value;

        ArrayList<ExprMetadata> parametersMetadata = CallExpr.parseArgsDeclaration(parser);
        ArrayList<ArgumentMetadata> parameters = new ArrayList<>();

        for (ExprMetadata metadata : parametersMetadata)
        {
            Expr id = metadata.getExpr();
            if (id.type != NodeType.Identifier)
            {
                throw new InvalidNodeException("Expected parameter identifier in parameters list.");
            }

            parameters.add(ArgumentMetadata.create(metadata.getType(), ((Identifier) id).get()));
        }

        parser.expect(TokenType.COLON, "Expecting ':' after function arguments declaration.");
        Type type = Type.parse(parser);

        parser.expect(TokenType.OPEN_BRACE, "Expecting '{' after function type declaration.");
        ArrayList<Statement> body = new ArrayList<>();

        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE)) {
            body.add(Statement.parse(parser));
        }

        parser.expect(TokenType.CLOSE_BRACE, "Expecting '}' after function body declaration.");

        return FunctionDeclaration.create(name, parameters, body, type);
    }

    public static FunctionDeclaration create(
        String identifier,
        ArrayList<ArgumentMetadata> parameters,
        ArrayList<Statement> body,
        Type returnType)
    {
        return new FunctionDeclaration(identifier, parameters, body, returnType);
    }


    // TODO: refactor these two functions below
    private String printParams(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n").repeat("\t", level)
                .append("[\n");
        for (ArgumentMetadata parameter : parameters)
        {
            ret.repeat("\t", next)
                    .append("name: ")
                    .append(parameter.getName())
                    .append(',')
                    .append('\n')
                    .repeat("\t", next)
                    .append("type: ")
                    .append(parameter.getType().print(next))
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
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "parameters: " + printParams(next) + ",\n" +
                "\t".repeat(next) + "body: " + printBody(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
