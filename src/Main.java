import Ast.Statements.Program;
import Entities.Common.Result.Error;
import Entities.Common.Result.ErrorOr;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Parser.AlreadyParsedException;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Runtime.*;

void main(String[] args)
    throws AlreadyParsedException,
        IOException,
        InvalidTokenException,
        InvalidArgumentException,
        AlreadyDeclaredVariableException
{
    if (args.length == 0)
    {
        IO.println("Tom v1.0");
        REPL.run();
    }

    if (args.length > 1)
    {
        throw new InvalidArgumentException("Número incorreto de argumentos informado.");
    }

    String fileName = args[0]; // "./main.tom";
    String content;

    {
        File file = new File(fileName);
        if (!file.exists())
        {
            throw new FileNotFoundException("Arquivo " + fileName + " não encontrado.");
        }

        try (FileReader reader = new FileReader(file))
        {
            content = reader.readAllAsString();
            fileName = file.getAbsolutePath();
        }
    }

    ErrorOr<Program> initialization = Program.initialize(content, fileName);

    if (initialization.isError())
    {
        Error err = initialization.error;
        System.err.println(err.getMessage());
        if (err.getLocation() != null)
        {
            System.err.println(err.getLocation());
        }
        System.exit(err.getExit());
    }

    Program program = initialization.value;
    Interpreter.evaluate(program, Environment.create());
}




