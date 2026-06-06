package Runtime.Evaluate.Factory.UnaryExpr;

import Ast.Expressions.UnaryExpr;
import Entities.Abstractions.Evaluate.Strategies.UnaryExprStrategy;
import Entities.Constants.ReservedKeys;
import Entities.Exceptions.Evaluate.InvalidUnaryExpression;
import Runtime.Evaluate.Strategies.UnaryExpr.AdditiveUnaryStrategy;
import Runtime.Evaluate.Strategies.UnaryExpr.FreezeUnaryStrategy;
import Runtime.Evaluate.Strategies.UnaryExpr.NotUnaryStrategy;

public abstract class UnaryExprFactory
{
    public static UnaryExprStrategy build(UnaryExpr expr)
    {
        if (ReservedKeys.Not.equals(expr.operator))
        {
            return new NotUnaryStrategy();
        }

        if (ReservedKeys.Freeze.equals(expr.operator))
        {
            return new FreezeUnaryStrategy();
        }

        if (ReservedKeys.Minus.equals(expr.operator) || ReservedKeys.Plus.equals(expr.operator))
        {
            return new AdditiveUnaryStrategy();
        }

        throw new InvalidUnaryExpression();
    }
}
