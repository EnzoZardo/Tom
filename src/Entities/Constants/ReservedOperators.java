package Entities.Constants;

import Entities.Enums.Lexer.TokenType;
import Lexer.Tokens.Token;

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
        ReservedKeys.Difference,
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
        return ReservedKeys.Not.equals(operator)
            || ReservedKeys.Minus.equals(operator)
            || ReservedKeys.Plus.equals(operator);
    }

    public static boolean isAdditiveOperator(String operator)
    {
        return ReservedKeys.Plus.equals(operator) || ReservedKeys.Minus.equals(operator);
    }

    public static boolean isNumericOperator(String operator)
    {
        return ReservedKeys.Mod.equals(operator)
            || ReservedKeys.Plus.equals(operator)
            || ReservedKeys.Minus.equals(operator)
            || ReservedKeys.Division.equals(operator)
            || ReservedKeys.Multiplication.equals(operator)
            || ReservedKeys.IntegerDivision.equals(operator);
    }

    public static boolean isBooleanOperator(String operator)
    {
        return ReservedKeys.GreaterOrEqual.equals(operator)
            || ReservedKeys.MinorOrEqual.equals(operator)
            || ReservedKeys.Difference.equals(operator)
            || ReservedKeys.Equality.equals(operator)
            || ReservedKeys.Greater.equals(operator)
            || ReservedKeys.Minor.equals(operator)
            || ReservedKeys.And.equals(operator)
            || ReservedKeys.Or.equals(operator)
            || ReservedKeys.In.equals(operator);
    }

    public static Token token(String value)
    {
        if (ReservedKeys.Equals.equals(value)) {
            return Token.create(TokenType.EQUALS, value);
        }

        return Token.create(TokenType.BINARY_OPERATOR, value);
    }
}
