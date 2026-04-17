package Entities.Common.Result;

public class ResultVoid extends ResultBase
{
    public ResultVoid()
    {
        super();
    }

    public ResultVoid(Error error)
    {
        super(error);
    }

    public static ResultVoid Ok()
    {
        return new ResultVoid();
    }

    public static ResultVoid Fail(Error error)
    {
        return new ResultVoid(error);
    }

    public static ResultVoid Fail(String message)
    {
        return new ResultVoid(Error.create(message));
    }

    public <T> ResultValue<T> toResultValue()
    {
        if (isFailure())
        {
            return ResultValue.Fail(error);
        }
        return ResultValue.Ok(null);
    }
}