package Parser;

import Ast.Statements.*;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.*;
import Entities.Exceptions.Parser.AlreadyParsedException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Lexer.Lexer;
import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;

import java.util.ArrayList;

public class Parser
{
    public final ArrayList<Token> tokens;
    public int tokenIndex = 0;

    public Parser(char[] content) throws AlreadyParsedException, InvalidTokenException
    {
        Lexer lexer = Lexer.create(content);
        tokens = lexer.tokenize();
    }

    public static Parser create(char[] content) throws AlreadyParsedException, InvalidTokenException
    {
        return new Parser(content);
    }

    public Program build() throws InvalidArgumentException, InvalidTokenException
    {
        Program program = Program.create();
        while (notEof())
        {
            program.addStatement(Statement.parse(this));
        }
        return program;
    }

    public Token expect(TokenType token, String error)
    {
        Token prev = consume();
        if (prev.type != token)
        {
            if (error.contains("%s"))
            {
                System.err.printf((error) + "%n", prev.value);
            } else
            {
                System.err.println(error);
            }

            System.err.println(prev);
            System.exit(1);
        }
        return prev;
    }

    public boolean notEof()
    {
        return !peekIs(TokenType.EOF);
    }

    private Token peek()
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
