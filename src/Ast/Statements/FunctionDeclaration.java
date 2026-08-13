package Ast.Statements;

import Ast.Expressions.CallExpr;
import Entities.Abstractions.Ast.Expr;
import Ast.Expressions.Identifier;
import Entities.Common.Result.ErrorType;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Lexer.TokenType;
import Entities.Exceptions.Parser.ParsingException;
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
    public ArrayList<String> typeParameters;

    protected FunctionDeclaration(
        String identifier,
        ArrayList<ArgumentMetadata> parameters,
        ArrayList<Statement> body,
        Type returnType,
        ArrayList<String> typeParameters)
    {
        super(NodeType.FunctionDeclaration);
        this.typeParameters = typeParameters;
        this.identifier = identifier;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    public static FunctionDeclaration create(
        String identifier,
        ArrayList<ArgumentMetadata> parameters,
        ArrayList<Statement> body,
        Type returnType,
        ArrayList<String> typeParameters)
    {
        return new FunctionDeclaration(identifier, parameters, body, returnType, typeParameters);
    }

    public static FunctionDeclaration parse(Parser parser)
    {
        parser.consume();
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos um nome para a " +
            "função declarada, mas recebemos outro símbolo no código - %s");
        String name = identifierToken.value;

        ArrayList<ArgumentMetadata> parameters = new ArrayList<>();
        ArrayList<String> typeParameters = new ArrayList<>();

        if (parser.peekIs(TokenType.BINARY_OPERATOR) && ReservedKeys.Minor.equals(parser.peekValue()))
        {
            //TODO: modularizar esse cara
            parser.consume();
            Token paramToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do parâmetro de tipo.");
            typeParameters.add(paramToken.value);
            while (parser.peekIs(TokenType.COMMA))
            {
                parser.consume();
                paramToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do parâmetro de tipo.");
                typeParameters.add(paramToken.value);
            }
            Token close = parser.expect(TokenType.BINARY_OPERATOR, "Esperávamos '>' para fechar a lista de parâmetros de tipo.");

            if (!ReservedKeys.Greater.equals(close.value))
                throw new ParsingException("Esperávamos '>' para fechar a lista de parâmetros de tipo.",
                        ErrorType.ParsingError, close.location);
        }

        ArrayList<ExprMetadata> parametersMetadata = CallExpr.parseArgsDeclaration(parser);

        for (ExprMetadata metadata : parametersMetadata)
        {
            Expr identifier = metadata.getExpr();
            if (identifier.type != NodeType.Identifier)
                throw new ParsingException(String.format("Esperávamos o nome do parâmetro de nossa função %s, " +
                        "mas recebemos outro símbolo no nosso código", name));

            parameters.add(ArgumentMetadata.create(metadata.getType(), ((Identifier) identifier).value));
        }

        parser.expect(TokenType.COLON,
            "Esperávamos dois pontos - : - para o nome de um parâmetro de nossa função, mas recebemos " +
            "outro símbolo no código - %s");

        Type type = Type.parse(parser);

        parser.expect(TokenType.OPEN_BRACE, "Esperávamos '{' para analisarmos o corpo da função.");
        ArrayList<Statement> body = new ArrayList<>();

        parser.context.enterFunction();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            body.add(Statement.parse(parser));
        }
        parser.context.outFunction();

        parser.expect(TokenType.CLOSE_BRACE, "Esperávamos '}' para fecharmos o corpo de uma função.");
        return FunctionDeclaration.create(name, parameters, body, type, typeParameters);
    }

    private String printParams(int level)
    {
        final int next = level + 1;
        StringBuilder ret = new StringBuilder("\n")
            .repeat("\t", level)
            .append("[\n");

        for (ArgumentMetadata parameter : parameters)
            ret.repeat("\t", next)
                .append("name: ")
                .append(parameter.getName())
                .append(",\n")
                .repeat("\t", next)
                .append("type: ")
                .append(parameter.getType().print(next))
                .append('\n');

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
            ret.repeat("\t", next)
                .append(statement.print(next))
                .append(',');

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
            "\t".repeat(next) + "body: " + printBody(next) + "\n" +
            "\t".repeat(level) + "}";
    }
}
