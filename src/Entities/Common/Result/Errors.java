package Entities.Common.Result;

public abstract class Errors
{
    public static <T> ErrorOr<T> invalidCall(String message)
    {
        return ErrorOr.Fail(message, ErrorType.InvalidCall);
    }

    public static <T> ErrorOr<T> unexpectedSymbol(char symbol)
    {
        return ErrorOr.Fail(String.format("Símbolo inesperado %s.", symbol), ErrorType.UnexpectedSymbol);
    }

    public static <T> ErrorOr<T> alreadyTokenized()
    {
        return ErrorOr.Fail("Conteúdo já foi transformado em símbolo.", ErrorType.AlreadyTokenized);
    }

    public static <T> ErrorOr<T> parsingError(String message)
    {
        return ErrorOr.Fail(message, ErrorType.ParsingError);
    }
}
