package Ast.Statements;

import Ast.Expressions.CallExpr;
import Entities.Abstractions.Ast.Expr;
import Ast.Expressions.Identifier;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;
import Entities.Metadata.ArgumentMetadata;
import Entities.Metadata.ExprMetadata;

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

    public static FunctionDeclaration create(
        String identifier,
        ArrayList<ArgumentMetadata> parameters,
        ArrayList<Statement> body,
        Type returnType)
    {
        return new FunctionDeclaration(identifier, parameters, body, returnType);
    }

    public static Statement parse(Parser parser) throws InvalidArgumentException
    {
        parser.consume();
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos receber o nome da função.");
        String name = identifierToken.value;

        ArrayList<ExprMetadata> parametersMetadata = CallExpr.parseArgsDeclaration(parser);
        ArrayList<ArgumentMetadata> parameters = new ArrayList<>();

        for (ExprMetadata metadata : parametersMetadata)
        {
            Expr identifier = metadata.getExpr();
            if (identifier.type != NodeType.Identifier)
            {
                throw new InvalidNodeException("Esperávamos o nome do argumento da função.");
            }

            parameters.add(ArgumentMetadata.create(metadata.getType(), ((Identifier) identifier).value));
        }

        parser.expect(TokenType.COLON, "Esperávamos ':' para declararmos o tipo de " +
                "retorno de uma função função.");

        Type type = Type.parse(parser);

        parser.expect(TokenType.OPEN_BRACE, "Esperávamos '{' para analisarmos o corpo da função.");
        ArrayList<Statement> body = new ArrayList<>();

        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE)) {
            body.add(Statement.parse(parser));
        }

        parser.expect(TokenType.CLOSE_BRACE, "Esperávamos '}' para fecharmos o corpo dee uma função.");
        return FunctionDeclaration.create(name, parameters, body, type);
    }

    private String printParams(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
            .repeat("\t", level)
            .append("[\n");

        for (ArgumentMetadata parameter : parameters)
        {
            ret.repeat("\t", next)
                .append("name: ")
                .append(parameter.getName())
                .append(",\n")
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
        StringBuilder ret = new StringBuilder("\n")
            .repeat("\t", level)
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
        return "\n" +
            "\t".repeat(level) + "{\n" +
            "\t".repeat(next) + "type: " + type.toString() + ",\n" +
            "\t".repeat(next) + "identifier: " + identifier + ",\n" +
            "\t".repeat(next) + "parameters: " + printParams(next) + ",\n" +
            "\t".repeat(next) + "body: " + printBody(next) + ",\n" +
            "\t".repeat(level) + "}";
    }
}
