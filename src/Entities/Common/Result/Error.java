package Entities.Common.Result;

public class Error
{
    private final String message;

    private Error(String message)
    {
        this.message = message;
    }

    public static Error create(String message)
    {
        return new Error(message);
    }

    public String getMessage()
    {
        return message;
    }

    public <T> ResultValue<T> toResultValue()
    {
        return ResultValue.Fail(this);
    }

    public ResultVoid toResultVoid()
    {
        return ResultVoid.Fail(this);
    }
}
