package Ast.Statements;

import Entities.Abstractions.Ast.Expr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.InvalidTokenException;
import Entities.Exceptions.NullConstantException;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Parser.Parser;

public class VariableDeclaration extends Statement
{
    public Type expectedType;
    public Expr value;
    public boolean constant;
    public String identifier;

    protected VariableDeclaration(
            Expr value,
            Type expectedType,
            String identifier,
            boolean constant)
    {
        super(NodeType.VariableDeclaration);
        this.expectedType = expectedType;
        this.value = value;
        this.constant = constant;
        this.identifier = identifier;
    }

    public static VariableDeclaration parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        boolean isConstant = parser.consume().type == TokenType.CONSTANT;
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Expecting identifier name following var/const.");
        String identifier = identifierToken.value;

        parser.expect(TokenType.COLON, "Expecting ':' on after variable declaration");
        Type type = Type.parse(parser);

        if (parser.peekIs(TokenType.SEMICOLON))
        {
            parser.consume();
            NullConstantException.ThrowIf(isConstant);
            return VariableDeclaration.notInstanced(identifier, type);
        }
        parser.expect(TokenType.EQUALS, "Expecting equals token to declare a variable.");
        return VariableDeclaration.create(Expr.parse(parser), type, identifier, isConstant);
    }

    public static VariableDeclaration create(
            Expr value,
            Type type,
            String identifier,
            boolean constant)
    {
        return new VariableDeclaration(value, type, identifier, constant);
    }

    public static VariableDeclaration notInstanced(
            String identifier, Type type)
    {
        return new VariableDeclaration(null, type, identifier, false);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "type: " + type.toString() + ",\n" +
                "\t".repeat(next) + "value: " + (value == null ?  ReservedKeys.Null : value.print(next)) + ",\n" +
                "\t".repeat(next) + "constant: " + constant + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "expectedType: " + expectedType.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
