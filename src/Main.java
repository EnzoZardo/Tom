import Ast.Statements.Program;
import Entities.Exceptions.Parser.LexingException;
import Entities.Exceptions.Parser.ParsingException;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.InvalidArgumentException;
import Runtime.*;

void main(String[] args)
    throws IOException,
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

    String fileName = args[0];
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

    try
    {
        Program program = Program.initialize(content, fileName);
        //IO.print(program);
        Interpreter.evaluate(program, Environment.create());
    }
    //TODO: fix
    catch (ParsingException e)
    {
        System.err.println(e.getMessage());
        if (e.getLocation() != null)
        {
            System.err.println(e.getLocation());
        }
        System.exit(e.getExit());
    }
    catch (LexingException e)
    {
        System.err.println(e.getMessage());
        if (e.getLocation() != null)
        {
            System.err.println(e.getLocation());
        }
        System.exit(e.getExit());
    }
}




