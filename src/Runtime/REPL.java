package Runtime;

import Ast.Statements.Program;
import Entities.Constants.ReservedKeys;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Lexer.Tokens.PonctuationToken;
import Parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

public class REPL
{
    private final BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    private final Queue<String> lastOpen;
    private boolean stop;
    private boolean isParenthesisOpen;
    private boolean isBracketOpen;
    private boolean isCommentOpen;
    private boolean isBraceOpen;
    private boolean isQuoteOpen;

    private REPL() {
        stop = false;
        lastOpen = new LinkedList<>();
        isQuoteOpen = false;
        isBraceOpen = false;
        isBracketOpen = false;
        isCommentOpen = false;
        isParenthesisOpen = false;
    }

    private void validateLine(List<Character> line)
    {
        boolean isOpeningComments = false;
        boolean isClosingComments = false;
        for (char c : line)
        {
            if (isClosingComments)
            {
                isClosingComments = false;
                if (PonctuationToken.isSlash(c))
                {
                    isCommentOpen = false;
                }
            }

            if (isCommentOpen && PonctuationToken.isAsterisc(c)) {
                isClosingComments = true;
                continue;
            }

            if (isOpeningComments) {
                isOpeningComments = false;
                if (PonctuationToken.isAsterisc(c))
                {
                    isCommentOpen = true;
                }
                continue;
            }

            if (!isQuoteOpen && !isCommentOpen && PonctuationToken.isSlash(c)) {
                isOpeningComments = true;
                continue;
            }

            if (!isCommentOpen && PonctuationToken.isQuotationMark(c))
            {
                isQuoteOpen = !isQuoteOpen;
                continue;
            }

            if (!isCommentOpen && !isQuoteOpen && PonctuationToken.isOpenBrace(c))
            {
                isBraceOpen = true;
                lastOpen.add("chaves");
                continue;
            }

            if (!isCommentOpen && !isQuoteOpen && PonctuationToken.isOpenBrackets(c))
            {
                isBracketOpen = true;
                lastOpen.add("colchetes");
                continue;
            }

            if (!isCommentOpen && !isQuoteOpen && PonctuationToken.isOpenParenthesis(c))
            {
                isParenthesisOpen = true;
                lastOpen.add("parênteses");
                continue;
            }

            if (!isCommentOpen && !isQuoteOpen && isBraceOpen && PonctuationToken.isCloseBrace(c))
            {
                isBraceOpen = false;
                lastOpen.poll();
                continue;
            }

            if (!isCommentOpen && !isQuoteOpen && isBracketOpen && PonctuationToken.isCloseBrackets(c))
            {
                isBracketOpen = false;
                lastOpen.poll();
                continue;
            }

            if (!isCommentOpen && !isQuoteOpen && isParenthesisOpen && PonctuationToken.isCloseParenthesis(c))
            {
                isParenthesisOpen = false;
                lastOpen.poll();
            }
        }
    }

    private List<Character> readLine(String hint) throws IOException
    {
        String line = "";

        while (line.isBlank())
        {
            IO.print(hint);
            line = bf.readLine();
        }

        return Arrays
            .stream(line.split(""))
            .map(x -> x.toCharArray()[0])
            .toList();
    }


    private char[] getInput() throws IOException
    {
        List<Character> line = new ArrayList<>(readLine("$ "));

        List<Character> complement;
        validateLine(line);

        while (isQuoteOpen
            || isBraceOpen
            || isBracketOpen
            || isParenthesisOpen
            || isCommentOpen)
        {
            if (isCommentOpen)
            {
                complement = readLine("comentário $ ");
                validateLine(complement);
                line.addAll(complement);
                continue;
            }

            if (isQuoteOpen)
            {
                complement = readLine("aspas $ ");
                validateLine(complement);
                line.addAll(complement);
                continue;
            }

            complement = readLine(lastOpen.peek() + " $ ");
            validateLine(complement);
            line.addAll(complement);
        }

        char[] charArray = new char[line.size()];
        for (int i = 0; i < line.size(); i++) {
            charArray[i] = line.get(i);
        }

        return charArray;
    }

    private boolean mustStop()
    {
        return stop;
    }

    public static void run() throws AlreadyDeclaredVariableException
    {
        REPL repl = new REPL();
        Environment env = Environment.create();

        while (!repl.mustStop())
        {
            try
            {
                Parser parser = Parser.create(repl.getInput());
                Program program = parser.build();
                IO.println(Interpreter.evaluate(program, env));
            }
            catch (Exception ex)
            {
                System.err.println("Ocorreu um erro: " + ex);
            }
        }

        System.exit(0);
    }

}
