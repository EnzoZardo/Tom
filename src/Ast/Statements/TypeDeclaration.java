package Ast.Statements;

import Ast.Enums.NodeType;
import Ast.Statements.Types.Type;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Exceptions.NullConstantException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
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

    public static TypeDeclaration parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        parser.consume();
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Expecting identifier name following type statement start.");
        String identifier = identifierToken.value;

        parser.expect(TokenType.EQUALS, "Expecting equals token to declare a variable.");
        return TypeDeclaration.create(Type.parse(parser), identifier);
    }

    public static TypeDeclaration create(
            Type value,
            String identifier)
    {
        return new TypeDeclaration(value, identifier);
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
