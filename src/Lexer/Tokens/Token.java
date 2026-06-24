package Lexer.Tokens;

import Entities.Common.Location.FileLocation;
import Entities.Common.Location.LocationPoint;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Lexer.TokenType;

public class Token
{
    public TokenType type;
    public LocationPoint location;
    public String value;

    protected Token(TokenType type, String value, LocationPoint location)
    {
        this.type = type;
        this.value = value;
        this.location = location;
    }

    public static Token create(TokenType type, String value, LocationPoint location)
    {
        return new Token(type, value, location);
    }

    public static Token create(TokenType type, String value, FileLocation start, FileLocation end, String file)
    {
        return new Token(type, value, LocationPoint.create(start, end, file));
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

    @Override
    public String toString()
    {
        if (value != null)
        {
            return "{ " + type.name() + ": " + value + ", localização: " + location + " }";
        }

        return type.name();
    }
}
