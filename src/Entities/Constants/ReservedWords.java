package Entities.Constants;

import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;
import Lexer.Tokens.TokenFileLocation;
import Lexer.Tokens.TokenLocation;

import java.util.HashMap;
import java.util.Map;

public record ReservedWords()
{
    private final static Map<String, TokenType> relations = new HashMap<>()
    {{
        put(ReservedKeys.And, TokenType.BINARY_OPERATOR);
        put(ReservedKeys.Or, TokenType.BINARY_OPERATOR);
        put(ReservedKeys.In, TokenType.BINARY_OPERATOR);
        put(ReservedKeys.Constant, TokenType.CONSTANT);
        put(ReservedKeys.Function, TokenType.FUNCTION);
        put(ReservedKeys.Variable, TokenType.DECLARE);
        put(ReservedKeys.Return, TokenType.RETURN);
        put(ReservedKeys.While, TokenType.WHILE);
        put(ReservedKeys.Type, TokenType.TYPE);
        put(ReservedKeys.Each, TokenType.EACH);
        put(ReservedKeys.Else, TokenType.ELSE);
        put(ReservedKeys.For, TokenType.FOR);
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
