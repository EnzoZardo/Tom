package Ast.Statements;

import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class TypeDeclaration extends Statement
{
    public Type value;
    public String identifier;

    protected TypeDeclaration(
        Type value,
        String identifier)
    {
        super(NodeType.TypeDeclaration);
        this.value = value;
        this.identifier = identifier;
    }

    public static TypeDeclaration create(
        Type value,
        String identifier)
    {
        return new TypeDeclaration(value, identifier);
    }

    public static ErrorOr<TypeDeclaration> parse(Parser parser)
    {
        parser.consume();
        ErrorOr<Token> identifierTokenOr = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do tipo em sua declaração.");
        if (identifierTokenOr.isError()) return identifierTokenOr.propagateError();
        String identifier = identifierTokenOr.value.value;

        ErrorOr<Token> equalsOr = parser.expect(TokenType.EQUALS, "Esperávamos '=' para declararmos o tipo " + identifier +".");
        if (equalsOr.isError()) return equalsOr.propagateError();
        ErrorOr<Type> typeOr = Type.parse(parser);
        if (typeOr.isError()) return typeOr.propagateError();
        return ErrorOr.Success(TypeDeclaration.create(typeOr.value, identifier));
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + "\n" +
                "\t".repeat(level) + "}";
    }
}
