package Constants;

import Lexer.Types.Enums.TokenType;
import Lexer.Types.Token;

import java.util.Set;

public record ReservedOperators()
{
    private final static Set<String> relations = Set.of(
        ReservedKeys.Not,
        ReservedKeys.Mod,
        ReservedKeys.Plus,
        ReservedKeys.Minus,
        ReservedKeys.Minor,
        ReservedKeys.Equals,
        ReservedKeys.Greater,
        ReservedKeys.Equality,
        ReservedKeys.Division,
        ReservedKeys.MinorOrEqual,
        ReservedKeys.GreaterOrEqual,
        ReservedKeys.Multiplication,
        ReservedKeys.IntegerDivision
    );

    public static boolean isReserved(String value)
    {
        return relations.contains(value);
    }

    public static boolean isUnary(String operator)
    {
        return ReservedKeys.Not.equals(operator) || ReservedKeys.Minus.equals(operator) || ReservedKeys.Plus.equals(operator);
    }

    public static boolean isBooleanOperator(String operator)
    {
        return ReservedKeys.GreaterOrEqual.equals(operator)
                || ReservedKeys.MinorOrEqual.equals(operator)
                || ReservedKeys.Equality.equals(operator)
                || ReservedKeys.Greater.equals(operator)
                || ReservedKeys.And.equals(operator)
                || ReservedKeys.Minor.equals(operator)
                || ReservedKeys.Or.equals(operator);
    }

    public static boolean isAdditiveOperator(String operator)
    {
        return ReservedKeys.Plus.equals(operator) || ReservedKeys.Minus.equals(operator);
    }

    public static Token token(String value)
    {
        if (ReservedKeys.Equals.equals(value)) {
            return Token.create(TokenType.EQUALS, value);
        }
        return Token.create(TokenType.BINARY_OPERATOR, value);
    }
}
