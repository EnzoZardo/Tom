package Entities.Constants;

public record ReservedKeys()
{
    /* #region Limits Definers */
    public static final char CloseParenthesis = ')';
    public static final char OpenParenthesis = '(';
    public static final char CloseBrackets = ']';
    public static final char OpenBrackets = '[';
    public static final char CloseBrace = '}';
    public static final char OpenBrace = '{';
    /* #endregion */

    /* #refion Punctuation Tokens */
    public static final char Interrogation = '?';
    public static final char Backslash = '\\';
    public static final char Semicolon = ';';
    public static final char Quote = '"';
    public static final char Colon = ':';
    public static final char Comma = ',';
    public static final char Dot = '.';
    /* #endregion */

    /* Comment's Tokens */
    public static final String CloseMultiLineComment = "*/";
    public static final String OpenMultiLineComment = "/*";
    public static final String InlineComment = "#";
    /* #endregion */

    /* #region Reserved Keys */
    public static final String Continue = "continue";
    public static final String Static = "estatico";
    public static final String True = "verdadeiro";
    public static final String While = "enquanto";
    public static final String Return = "retorne";
    public static final String Protected = "prot";
    public static final String Function = "fun";
    public static final String Variable = "var";
    public static final String Constant = "con";
    public static final String Class = "classe";
    public static final String Private = "priv";
    public static final String False = "falso";
    public static final String Public = "publ";
    public static final String Else = "senao";
    public static final String Break = "pare";
    public static final String Each = "cada";
    public static final String This = "isso";
    public static final String Type = "tipo";
    public static final String For = "para";
    public static final String New = "novo";
    public static final String If = "se";
    /* #endregion */

    /* #region Native Functions */
    public static final String Interval = "intervalo";
    public static final String Convert = "converte";
    public static final String Print = "escreva";
    public static final String Read = "leia";
    /* #endregion */

    /* #region Some Unary/Binary Operators */
    public static final String IntegerDivision = "//";
    public static final String GreaterOrEqual = ">=";
    public static final String Multiplication = "*";
    public static final String MinorOrEqual = "<=";
    public static final String Freeze = "congele";
    public static final String Difference = "<>";
    public static final String Equality = "==";
    public static final String Division = "/";
    public static final String Greater = ">";
    public static final String Equals = "=";
    public static final String Minor = "<";
    public static final String Minus = "-";
    public static final String Not = "nao";
    public static final String Plus = "+";
    public static final String Mod = "%";
    public static final String And = "e";
    public static final String Or = "ou";
    public static final String In = "em";
    /* #endregion */

    /* #region Primitive Types */
    public static final String Integer = "inteiro";
    public static final String Boolean = "logico";
    public static final String Object = "objeto";
    public static final String String = "texto";
    public static final String Float = "real";
    public static final String Null = "nulo";
    /* #endregion */
}

