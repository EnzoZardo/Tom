package Runtime.Evaluate.Factory.UnaryExpr;

import Ast.Expressions.UnaryExpr;
import Entities.Abstractions.Evaluate.Strategies.UnaryExprStrategy;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Runtime.Evaluate.Strategies.UnaryExpr.AdditiveUnaryStrategy;
import Runtime.Evaluate.Strategies.UnaryExpr.FreezeUnaryStrategy;
import Runtime.Evaluate.Strategies.UnaryExpr.NotUnaryStrategy;

public abstract class UnaryExprFactory
{
    public static ErrorOr<UnaryExprStrategy> build(UnaryExpr expr)
    {
        if (ReservedKeys.Not.equals(expr.operator))
        {
            return ErrorOr.Success(new NotUnaryStrategy());
        }

        if (ReservedKeys.Freeze.equals(expr.operator))
        {
            return ErrorOr.Success(new FreezeUnaryStrategy());
        }

        if (ReservedKeys.Minus.equals(expr.operator) || ReservedKeys.Plus.equals(expr.operator))
        {
            return ErrorOr.Success(new AdditiveUnaryStrategy());
        }

        return ErrorOr.Fail("Expressão unária inválida.");
    }
}
