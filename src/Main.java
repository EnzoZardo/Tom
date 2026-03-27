import Ast.Statements.Program;
import Exceptions.AlreadyDeclaredVariableException;
import Exceptions.AlreadyParsedException;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Parser.Parser;
import Runtime.Environment;
import Runtime.Interpreter;

void main(String[] args) throws AlreadyParsedException, IOException, InvalidTokenException, InvalidArgumentException, AlreadyDeclaredVariableException
{
    if (args.length < 1)
    {
        throw new IllegalArgumentException("The file argument was not informed.");
    }

    File file = new File(args[0]);
    char[] content;

    if (!file.canRead())
    {
        throw new IllegalArgumentException("The given file does not allow reading.");
    }

    {
        FileReader reader = new FileReader(file);
        content = reader.readAllAsString()
                .toCharArray();
        reader.close();
    }

    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

    IO.println("Tom v1.0");

    Environment env = Environment.create();
    while (true)
    {
        IO.print("$ ");
        Parser parser = Parser.create(bf.readLine()
                .toCharArray());
        Program program = parser.build();
        //IO.println(program);
        IO.println(Interpreter.evaluate(program, env));

        IO.println("-------------\n\n");
    }
}
