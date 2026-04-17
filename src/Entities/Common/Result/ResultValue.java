package Entities.Common.Result;

public class ResultValue<T> extends ResultBase
{
    public T value = null;

    public ResultValue(T value)
    {
        super();
        this.value = value;
    }

    public ResultValue(Error error)
    {
        super(error);
    }

    public static <T> ResultValue<T> Ok(T value)
    {
        return new ResultValue<>(value);
    }

    public static <T> ResultValue<T> Fail(Error error)
    {
        return new ResultValue<>(error);
    }

    public static <T> ResultValue<T> Fail(String message)
    {
        return new ResultValue<>(Error.create(message));
    }

    public ResultVoid toResultVoid()
    {
        if (isFailure())
        {
            return ResultVoid.Fail(error);
        }
        return ResultVoid.Ok();
    }
}