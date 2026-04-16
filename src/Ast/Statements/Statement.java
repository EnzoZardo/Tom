package Ast.Statements;

import Ast.Statements.Expressions.Expr;
import Ast.Enums.NodeType;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Lexer.Types.Enums.TokenType;
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
