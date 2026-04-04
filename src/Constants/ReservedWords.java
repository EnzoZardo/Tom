package Constants;

import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;

import java.util.HashMap;
import java.util.Map;

public record ReservedWords()
{
    private final static Map<String, TokenType> relations = new HashMap<>()
    {{
        put(ReservedKeys.Variable, TokenType.DECLARE);
        put(ReservedKeys.Constant, TokenType.CONSTANT);
        put(ReservedKeys.Function, TokenType.FUNCTION);
        put(ReservedKeys.Return, TokenType.RETURN);
        put(ReservedKeys.Or, TokenType.BINARY_OPERATOR);
        put(ReservedKeys.And, TokenType.BINARY_OPERATOR);
    }};

    public static boolean isReserved(String value)
    {
        return relations.containsKey(value);
    }

    public static Token token(String value)
    {
        return Token.create(relations.get(value), value);
    }
}
