package Lexer;

import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Entities.Common.Location.FileLocation;

import java.util.ArrayList;

public class TokenBuffer
{
    private final ArrayList<Token> tokens;

    private TokenBuffer()
    {
        this.tokens = new ArrayList<>();
    }

    public static TokenBuffer create()
    {
        return new TokenBuffer();
    }

    public void emit(TokenType type, String value, FileLocation start, FileLocation end, String file)
    {
        tokens.add(Token.create(type, value, start, end, file));
    }

    public void emitReserved(Token token)
    {
        tokens.add(token);
    }

    public ArrayList<Token> toList()
    {
        return tokens;
    }
}