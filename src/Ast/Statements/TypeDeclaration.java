package Ast.Statements;

import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
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

    public static TypeDeclaration parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.consume();
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Esperávamos o nome do tipo em sua declaração.");
        String identifier = identifierToken.value;

        parser.expect(TokenType.EQUALS, "Esperávamos '=' para declararmos o tipo " + identifier +".");
        return TypeDeclaration.create(Type.parse(parser), identifier);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
