package Entities.Exceptions.Evaluate;

public class InvalidComputedObjectKeyType extends EvaluatingException
{
    public InvalidComputedObjectKeyType()
    {
        super("Tipo de chave de objeto inválido, somente são permitidos textos.");
    }
}
