package Parser;

import Ast.Statements.*;
import Ast.Types.Statement;
import Exceptions.*;
import Lexer.Lexer;
import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;

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
            System.err.println(error);
            System.err.println(prev);
            System.exit(1);
        }
        return prev;
    }

    public boolean notEof()
    {
        return !peekIs(TokenType.EOF);
    }

    public Token peek()
    {
        if (tokenIndex >= tokens.size())
        {
            return null;
        }
        return tokens.get(tokenIndex);
    }

    public String peekValue()
    {
        return peek().value;
    }

    public TokenType peekType()
    {
        return peek().type;
    }

    public boolean peekIs(TokenType token)
    {
        return peekType() == token;
    }

    public Token consume()
    {
        return tokens.get(tokenIndex++);
    }
}
