package Lexer.Types;

import Constants.ReservedKeys;
import Lexer.Types.Enums.TokenType;

public class PonctuationToken extends Token
{
    protected PonctuationToken(TokenType type, String value)
    {
        super(type, value);
    }

    public static boolean isSemicolon(char c)
    {
        return ReservedKeys.Semicolon == c;
    }

    public static boolean isOpenParenthesis(char c)
    {
        return ReservedKeys.OpenParenthesis == c;
    }

    public static boolean isCloseParenthesis(char c)
    {
        return ReservedKeys.CloseParenthesis == c;
    }

    public static boolean isOpenBrackets(char c)
    {
        return ReservedKeys.OpenBrackets == c;
    }

    public static boolean isCloseBrackets(char c)
    {
        return ReservedKeys.CloseBrackets == c;
    }

    public static boolean isOpenBrace(char c)
    {
        return ReservedKeys.OpenBrace == c;
    }

    public static boolean isCloseBrace(char c)
    {
        return ReservedKeys.CloseBrace == c;
    }

    public static boolean isColon(char c)
    {
        return ReservedKeys.Colon == c;
    }

    public static boolean isComma(char c)
    {
        return ReservedKeys.Comma == c;
    }

    public static boolean isDot(char c)
    {
        return ReservedKeys.Dot == c;
    }

    public static boolean isQuotationMark(char c)
    {
        return ReservedKeys.Quote == c;
    }

    public static boolean isBackslash(char c)
    {
        return ReservedKeys.Backslash == c;
    }
}
