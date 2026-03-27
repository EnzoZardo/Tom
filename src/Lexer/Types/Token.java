package Lexer.Types;

import Constants.ReservedKeys;
import Lexer.Types.Enums.TokenType;

public class Token
{
    public TokenType type;
    public String value;

    protected Token(TokenType type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public static Token create(TokenType type, String value)
    {
        return new Token(type, value);
    }

    public static boolean isNumeric(char c)
    {
        return Character.isDigit(c);
    }

    public static boolean isAlphabetic(char c)
    {
        return Character.isAlphabetic(c);
    }

    public static boolean isBinaryOperator(char c)
    {
        return ReservedKeys.Plus == c
                || ReservedKeys.Minus == c
                || ReservedKeys.Division == c
                || ReservedKeys.Multiplication == c
                || ReservedKeys.Mod == c;
    }

    public static boolean isEquals(char c)
    {
        return ReservedKeys.Equals == c;
    }

    public static boolean isIgnorable(char c)
    {
        return Character.isSpaceChar(c) || c == '\n' || c == '\t' || c == '\r';
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
            return type.name() + "=" + value;
        }
        return type.name();
    }
}
