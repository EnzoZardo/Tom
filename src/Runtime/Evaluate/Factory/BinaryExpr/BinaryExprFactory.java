package Runtime.Evaluate.Factory.BinaryExpr;

import Ast.Expressions.BinaryExpr;
import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Evaluate.Strategies.BinaryExpr.BooleanBinaryStrategy;
import Runtime.Evaluate.Strategies.BinaryExpr.NumericBinaryStrategy;
import Runtime.Evaluate.Strategies.BinaryExpr.StringBinaryStrategy;
import Runtime.Interpreter;
import Runtime.Environment;

public abstract class BinaryExprFactory
{
    public static ErrorOr<BinaryExprStrategy> build(BinaryExpr expr, Environment env)
            throws AlreadyDeclaredVariableException
    {
        RuntimeValue left = Interpreter.evaluate(expr.left, env);
        RuntimeValue right = Interpreter.evaluate(expr.right, env);
        String operator = expr.operator;

        if (NumericBinaryStrategy.canEvaluate(right, left, operator)) {
            return ErrorOr.Success(new NumericBinaryStrategy());
        }

        if (StringBinaryStrategy.canEvaluate(right, left, operator)) {
            return ErrorOr.Success(new StringBinaryStrategy());
        }

        if (BooleanBinaryStrategy.canEvaluate(operator)) {
            return ErrorOr.Success(new BooleanBinaryStrategy());
        }

        return ErrorOr.Fail("Operação binária informada ainda não é suportada.");
    }
}
