package Parser;

import Ast.Statements.*;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Common.Result.ErrorType;
import Entities.Parsing.ContextMemory;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;

import java.util.ArrayList;

public class Parser
{
    public final ArrayList<Token> tokens;
    public final ContextMemory context;
    public int tokenIndex = 0;

    private Parser(ArrayList<Token> tokens)
    {
        context = ContextMemory.create();
        this.tokens = tokens;
    }

    public static Parser create(ArrayList<Token> tokens)
    {
        return new Parser(tokens);
    }

    public ErrorOr<Program> build()
    {
        Program program = Program.create();
        while (notEof())
        {
            var stmtOr = Statement.parse(this);
            if (stmtOr.isError()) return stmtOr.propagateError();
            program.addStatement(stmtOr.value);
        }

        return ErrorOr.Success(program);
    }

    public ErrorOr<Token> expect(TokenType token, String error)
    {
        Token prev = consume();
        if (prev.type != token)
        {
            String message = error;

            if (error.contains("%s"))
                message = String.format((error) + "%n", prev.value);

            return ErrorOr.Fail(message, ErrorType.ParsingError, prev.location);
        }
        return ErrorOr.Success(prev);
    }

    public boolean notEof()
    {
        return !peekIs(TokenType.EOF);
    }

    public Token peek()
    {
        return peek(0);
    }

    public Token peek(int offset)
    {
        if (tokenIndex + offset >= tokens.size())
        {
            return null;
        }
        return tokens.get(tokenIndex + offset);
    }

    public String peekValue()
    {
        return peek().value;
    }

    public String peekValue(int offset)
    {
        return peek(offset).value;
    }

    public TokenType peekType(int offset)
    {
        return peek(offset).type;
    }

    public boolean peekIs(int offset, TokenType token)
    {
        return peekType(offset) == token;
    }

    public TokenType peekType()
    {
        return peek(0).type;
    }

    public boolean peekIs(TokenType token)
    {
        return peekType(0) == token;
    }

    public Token consume()
    {
        return tokens.get(tokenIndex++);
    }
}
