package Entities.Constants;

import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.TokenFileLocation;
import Lexer.Tokens.Token;
import java.util.HashMap;
import java.util.Map;

public record ReservedWords()
{
    private final static Map<String, TokenType> relations = new HashMap<>()
    {{
        put(ReservedKeys.Protected, TokenType.PROTECTION_MARKER);
        put(ReservedKeys.Private, TokenType.PROTECTION_MARKER);
        put(ReservedKeys.Public, TokenType.PROTECTION_MARKER);
        put(ReservedKeys.And, TokenType.BINARY_OPERATOR);
        put(ReservedKeys.Or, TokenType.BINARY_OPERATOR);
        put(ReservedKeys.In, TokenType.BINARY_OPERATOR);
        put(ReservedKeys.Constant, TokenType.CONSTANT);
        put(ReservedKeys.Function, TokenType.FUNCTION);
        put(ReservedKeys.Continue, TokenType.CONTINUE);
        put(ReservedKeys.Variable, TokenType.DECLARE);
        put(ReservedKeys.Return, TokenType.RETURN);
        put(ReservedKeys.Class, TokenType.CLASS);
        put(ReservedKeys.While, TokenType.WHILE);
        put(ReservedKeys.Break, TokenType.BREAK);
        put(ReservedKeys.Type, TokenType.TYPE);
        put(ReservedKeys.Each, TokenType.EACH);
        put(ReservedKeys.Else, TokenType.ELSE);
        put(ReservedKeys.For, TokenType.FOR);
        put(ReservedKeys.New, TokenType.NEW);
        put(ReservedKeys.If, TokenType.IF);
    }};

    public static boolean isReserved(String value)
    {
        return relations.containsKey(value);
    }

    public static Token token(String value, TokenFileLocation start, TokenFileLocation end)
    {
        return Token.create(relations.get(value), value, start, end);
    }
}
