package Entities.Abstractions.Ast;

import Ast.Statements.*;
import Entities.Enums.Ast.NodeType;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Enums.Lexer.TokenType;
import Parser.Parser;

public abstract class Statement
{
    public NodeType type;

    protected Statement(NodeType type)
    {
        this.type = type;
    }

    public static Statement parse(Parser parser) throws InvalidArgumentException
    {
        return switch (parser.peekType())
        {
            case BREAK -> Break.parse(parser);
            case WHILE -> While.parse(parser);
            case FOR -> ForEach.parse(parser);
            case RETURN -> Return.parse(parser);
            case IMPORT -> Import.parse(parser);
            case IF -> IfConditional.parse(parser);
            case CONTINUE -> Continue.parse(parser);
            case TYPE -> TypeDeclaration.parse(parser);
            case CLASS -> ClassDeclaration.parse(parser);
            case OPEN_BRACE -> ScopeDeclaration.parse(parser);
            case FUNCTION -> FunctionDeclaration.parse(parser);
            case DECLARE, CONSTANT -> VariableDeclaration.parse(parser);
            default -> Expr.parse(parser);
        };
    }

    public abstract String print(int level);
}
