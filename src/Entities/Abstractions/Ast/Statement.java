package Entities.Abstractions.Ast;

import Entities.Enums.Ast.NodeType;
import Ast.Statements.FunctionDeclaration;
import Ast.Statements.TypeDeclaration;
import Ast.Statements.VariableDeclaration;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.InvalidTokenException;
import Entities.Enums.Lexer.TokenType;
import Parser.Parser;

public abstract class Statement
{
    public NodeType type;

    protected Statement(NodeType type)
    {
        this.type = type;
    }

    public static Statement parse(Parser parser) throws InvalidArgumentException, InvalidTokenException
    {
        return switch (parser.peekType())
        {
            case TokenType.DECLARE, TokenType.CONSTANT -> VariableDeclaration.parse(parser);
            case TokenType.TYPE ->  TypeDeclaration.parse(parser);
            case TokenType.FUNCTION -> FunctionDeclaration.parse(parser);
            default -> Expr.parse(parser);
        };
    }

    public abstract String print(int level);
}
