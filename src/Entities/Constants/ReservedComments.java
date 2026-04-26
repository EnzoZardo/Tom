package Entities.Constants;

public record ReservedComments()
{
    public static boolean isInlineComment(char operator)
    {
        return ReservedKeys.InlineComment.equals(Character.toString(operator));
    }

    public static boolean isOpenMultiLineComment(char first, Character second)
    {
        return ReservedKeys.OpenMultiLineComment.equals(Character.toString(first) + second);
    }

    public static boolean isCloseMultiLineComment(char first, Character second)
    {
        return ReservedKeys.CloseMultiLineComment.equals(Character.toString(first) + second);
    }
}
