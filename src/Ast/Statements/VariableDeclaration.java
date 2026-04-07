package Ast.Statements;

import Ast.Types.Enums.NodeType;
import Ast.Types.Statement;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Exceptions.NullConstantException;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;
import Parser.Parser;

public class VariableDeclaration extends Statement
{
    public Expr value;
    public boolean constant;
    public String identifier;

    protected VariableDeclaration(
            Expr value,
            String identifier,
            boolean constant)
    {
        super(NodeType.VariableDeclaration);
        this.value = value;
        this.identifier = identifier;
        this.constant = constant;
    }

    public static VariableDeclaration parse(Parser parser) throws InvalidTokenException, InvalidArgumentException
    {
        boolean isConstant = parser.consume().type == TokenType.CONSTANT;
        Token identifierToken = parser.expect(TokenType.IDENTIFIER, "Expecting identifier name following let/const.");
        String identifier = identifierToken.value;

        if (parser.peekIs(TokenType.SEMICOLON))
        {
            parser.consume();
            NullConstantException.ThrowIf(isConstant);
            return VariableDeclaration.notInstanced(identifier);
        }
        parser.expect(TokenType.EQUALS, "Expecting equals token to declare a variable.");
        return VariableDeclaration.create(Expr.parse(parser), identifier, isConstant);
    }

    public static VariableDeclaration create(
            Expr value,
            String identifier,
            boolean constant)
    {
        return new VariableDeclaration(value, identifier, constant);
    }

    public static VariableDeclaration notInstanced(
            String identifier)
    {
        return new VariableDeclaration(null, identifier, false);
    }

    @Override
    public String print(int level)
    {
        final int next = level + 1;
        return "\n" + "\t".repeat(level) + "{\n" +
                "\t".repeat(next) + "node: " + type.toString() + ",\n" +
                "\t".repeat(next) + "constant: " + constant + ",\n" +
                "\t".repeat(next) + "identifier: " + identifier + ",\n" +
                "\t".repeat(next) + "value: " + value.print(next) + ",\n" +
                "\t".repeat(level) + "}";
    }
}
