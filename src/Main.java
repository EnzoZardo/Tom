
import Ast.Statements.Program;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Parser.AlreadyParsedException;
import Entities.Exceptions.InvalidArgumentException;
import Entities.Exceptions.Parser.InvalidTokenException;
import Parser.Parser;
import Runtime.*;

void main(String[] args) throws AlreadyParsedException, IOException, InvalidTokenException, InvalidArgumentException, AlreadyDeclaredVariableException
{
    IO.println("Tom v1.0");

    if (args.length == 0)
    {
        REPL.run();
    }

    if (args.length > 1)
    {
        throw new InvalidArgumentException("Número incorreto de argumentos informado");
    }

    final String fileName = args[0];
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
        }
    }

    Parser parser = Parser.create(content.toCharArray());
    Program program = parser.build();
    Interpreter.evaluate(program, Environment.create());
}




