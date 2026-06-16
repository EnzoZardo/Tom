package Entities.Common.Result;

public abstract class Errors
{
    public static <T> ErrorOr<T> invalidCall(String message)
    {
        return ErrorOr.Fail(message, ErrorType.InvalidCall);
    }
}
