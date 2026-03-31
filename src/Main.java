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
    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

    IO.println("Tom v1.0");

    Environment env = Environment.create();
    while (true)
    {
        IO.print("$ ");
        Parser parser = Parser.create(bf.readLine().toCharArray());
        Program program = parser.build();
        //IO.println(program);
        IO.println(Interpreter.evaluate(program, env));

        IO.println("-------------\n\n");
    }
}
