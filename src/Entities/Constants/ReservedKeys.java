package Entities.Constants;

public record ReservedKeys()
{
    // Limits Definers
    public static final char OpenParenthesis = '(';
    public static final char CloseParenthesis = ')';
    public static final char OpenBrackets = '[';
    public static final char CloseBrackets = ']';
    public static final char OpenBrace = '{';
    public static final char CloseBrace = '}';

    // Pontuation Tokens
    public static final char Semicolon = ';';
    public static final char Colon = ':';
    public static final char Comma = ',';
    public static final char Dot = '.';
    public static final char Backslash = '\\';
    public static final char Quote = '"';

    // Reserved Keys
    public static final String Return = "retorne";
    public static final String Function = "fun";
    public static final String Variable = "var";
    public static final String Constant = "con";
    public static final String While = "enquanto";
    public static final String Else = "senao";
    public static final String If = "se";
    public static final String Type = "tipo";
    public static final String True = "verdadeiro";
    public static final String False = "falso";

    // Native Functions
    public static final String Print = "escreva";

    // Binary Operators
    public static final String Equals = "=";
    public static final String Plus = "+";
    public static final String Minus = "-";
    public static final String Mod = "%";
    public static final String Multiplication = "*";
    public static final String Division = "/";
    public static final String IntegerDivision = "//";
    public static final String Greater = ">";
    public static final String GreaterOrEqual = ">=";
    public static final String Minor = "<";
    public static final String MinorOrEqual = "<=";
    public static final String Equality = "==";
    public static final String Difference = "<>";
    public static final String And = "e";
    public static final String Or = "ou";
    public static final String Not = "nao";

    // Primitive Types
    public static final String Float = "real";
    public static final String String = "texto";
    public static final String Object = "objeto";
    public static final String Boolean = "logico";
    public static final String Char = "caractere";
    public static final String Integer = "inteiro";
    public static final String Null = "nulo";

}

