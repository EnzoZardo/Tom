package Ast.Statements;

import Ast.Expressions.CallExpr;
import Entities.Abstractions.Ast.Expr;
import Ast.Expressions.Identifier;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
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

    public static ErrorOr<FunctionDeclaration> parse(Parser parser)
    {
        parser.consume();
        ErrorOr<Token> identifierTokenOr = parser.expect(TokenType.IDENTIFIER, "Esperávamos um nome para a " +
            "função declarada, mas recebemos outro símbolo no código - %s");
        if (identifierTokenOr.isError()) return identifierTokenOr.propagateError();
        String name = identifierTokenOr.value.value;

        ErrorOr<ArrayList<ExprMetadata>> paramsMetaOr = CallExpr.parseArgsDeclaration(parser);
        if (paramsMetaOr.isError()) return paramsMetaOr.propagateError();

        ArrayList<ExprMetadata> parametersMetadata = paramsMetaOr.value;
        ArrayList<ArgumentMetadata> parameters = new ArrayList<>();

        for (ExprMetadata metadata : parametersMetadata)
        {
            Expr identifier = metadata.getExpr();
            if (identifier.type != NodeType.Identifier)
                return ErrorOr.Fail(String.format("Esperávamos o nome do parâmetro de nossa função %s, " +
                        "mas recebemos outro símbolo no nosso código", name));

            parameters.add(ArgumentMetadata.create(metadata.getType(), ((Identifier) identifier).value));
        }

        ErrorOr<Token> colonOr = parser.expect(TokenType.COLON,
            "Esperávamos dois pontos - : - para o nome de um parâmetro de nossa função, mas recebemos " +
            "outro símbolo no código - %s");
        if (colonOr.isError()) return colonOr.propagateError();

        ErrorOr<Type> typeOr = Type.parse(parser);
        if (typeOr.isError()) return typeOr.propagateError();
        Type type = typeOr.value;

        ErrorOr<Token> openBraceOr = parser.expect(TokenType.OPEN_BRACE, "Esperávamos '{' para analisarmos o corpo da função.");
        if (openBraceOr.isError()) return openBraceOr.propagateError();
        ArrayList<Statement> body = new ArrayList<>();

        parser.context.enterFunction();
        while (parser.notEof() && !parser.peekIs(TokenType.CLOSE_BRACE))
        {
            var stmtOr = Statement.parse(parser);
            if (stmtOr.isError()) return stmtOr.propagateError();
            body.add(stmtOr.value);
        }
        parser.context.outFunction();

        ErrorOr<Token> closeBraceOr = parser.expect(TokenType.CLOSE_BRACE, "Esperávamos '}' para fecharmos o corpo de uma função.");
        if (closeBraceOr.isError()) return closeBraceOr.propagateError();
        return ErrorOr.Success(FunctionDeclaration.create(name, parameters, body, type));
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
