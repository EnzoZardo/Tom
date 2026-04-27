package Runtime;

import Ast.Statements.Program;
import Entities.Constants.ReservedKeys;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.InvalidArgumentException;
import Lexer.Tokens.PonctuationToken;
import Parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class REPL
{
    private final BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    private boolean stop;
    private boolean isParenthesisOpen;
    private boolean isBracketOpen;
    private boolean isBraceOpen;
    private boolean isQuoteOpen;
    private char lastOpen;

    private REPL() {
        stop = false;
        isQuoteOpen = false;
        isBraceOpen = false;
        isBracketOpen = false;
        isParenthesisOpen = false;
    }

    private void validateLine(List<Character> line)
    {
        for (char c : line)
        {
            if (PonctuationToken.isQuotationMark(c))
            {
                isQuoteOpen = !isQuoteOpen;
                lastOpen = ReservedKeys.Quote;
            }

            if (!isQuoteOpen && PonctuationToken.isOpenBrace(c))
            {
                isBraceOpen = true;
                lastOpen = ReservedKeys.OpenBrace;
            }

            if (!isQuoteOpen && PonctuationToken.isOpenBrackets(c))
            {
                isBracketOpen = true;
                lastOpen = ReservedKeys.OpenBrackets;
            }

            if (!isQuoteOpen && PonctuationToken.isOpenParenthesis(c))
            {
                isParenthesisOpen = true;
                lastOpen = ReservedKeys.OpenParenthesis;
            }

            if (!isQuoteOpen && isBraceOpen && PonctuationToken.isCloseBrace(c))
            {
                isBraceOpen = false;
            }

            if (!isQuoteOpen && isBracketOpen && PonctuationToken.isCloseBrackets(c))
            {
                isBracketOpen = false;
            }

            if (!isQuoteOpen && isParenthesisOpen && PonctuationToken.isCloseParenthesis(c))
            {
                isParenthesisOpen = false;
            }
        }
    }

    private char[] getInput() throws IOException
    {
        IO.print("$ ");
        List<Character> line = new ArrayList<>(Arrays
                .stream(bf.readLine()
                        .split(""))
                .map(x -> x.toCharArray()[0])
                .toList());

        List<Character> complement;
        validateLine(line);

        while (isQuoteOpen
            || isBraceOpen
            || isBracketOpen
            || isParenthesisOpen)
        {
            if (isQuoteOpen)
            {
                IO.print("aspas $ ");
                complement = Arrays
                        .stream(bf.readLine()
                                .split(""))
                        .map(x -> x.toCharArray()[0])
                        .toList();
                validateLine(complement);
                line.addAll(complement);
                continue;
            }

            String hint = switch (lastOpen) {
                case ReservedKeys.OpenBrace -> "chaves";
                case ReservedKeys.OpenBrackets -> "colchetes";
                default -> "parenteses";
            };

            IO.print(hint + " $ ");
            complement = Arrays
                    .stream(bf.readLine()
                            .split(""))
                    .map(x -> x.toCharArray()[0])
                    .toList();
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
