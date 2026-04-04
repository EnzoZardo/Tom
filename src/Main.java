import Ast.Statements.Program;
import Exceptions.AlreadyParsedException;
import Exceptions.InvalidArgumentException;
import Exceptions.InvalidTokenException;
import Parser.Parser;

void main(String[] args) throws AlreadyParsedException, IOException, InvalidTokenException, InvalidArgumentException
{
//    BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    IO.println("Tom v1.0");
    final String fileName = "./main.tom";
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
    IO.println(program);
//    Interpreter.evaluate(program, Environment.create());

//    while (true)
//    {
//        IO.print("$ ");
//        Parser parser = Parser.create(bf.readLine().toCharArray());
//        Program program = parser.build();
//        IO.println(Interpreter.evaluate(program, env));
//    }
}
