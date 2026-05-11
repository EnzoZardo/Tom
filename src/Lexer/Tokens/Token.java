package Lexer.Tokens;

import Entities.Constants.ReservedKeys;
import Entities.Enums.Lexer.TokenType;

public class Token
{
    public TokenType type;
    public TokenLocation location;
    public String value;

    protected Token(TokenType type, String value, TokenLocation location)
    {
        this.type = type;
        this.value = value;
        this.location = location;
    }

    public static Token create(TokenType type, String value, TokenLocation location)
    {
        return new Token(type, value, location);
    }

    public static Token create(TokenType type, String value, TokenFileLocation start, TokenFileLocation end)
    {
        return new Token(type, value, TokenLocation.create(start, end));
    }

    public static boolean isNumeric(char c)
    {
        return Character.isDigit(c);
    }

    public static boolean isAlphabetic(char c)
    {
        return Character.isAlphabetic(c);
    }

    public static boolean isAlphabeticOperator(char c)
    {
        return ReservedKeys.And.charAt(0) == c || ReservedKeys.Or.charAt(0) == c;
    }

    public static boolean isEquals(char c)
    {
        return ReservedKeys.Equals.equals(Character.toString(c));
    }

    public static boolean isIgnorable(char c)
    {
        return Character.isSpaceChar(c) || c == '\t' || c == '\r';
    }

    public static boolean isNewLine(char c)
    {
        return c == '\n';
    }

    public static boolean isNewLine(String c)
    {
        return "\n".equals(c);
    }

    public char Char()
    {
        char[] c = value.toCharArray();
        if (c.length == 0)
        {
            return '\0';
        }
        return c[0];
    }

    @Override
    public String toString()
    {
        if (value != null)
        {
            return "{ " + type.name() + " = " + value + ", localização = " + location + " }";
        }

        return type.name();
    }
}
