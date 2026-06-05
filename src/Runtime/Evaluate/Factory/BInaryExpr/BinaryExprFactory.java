package Runtime.Evaluate.Factory.BInaryExpr;

import Ast.Expressions.BinaryExpr;
import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Runtime.Evaluate.Strategies.BooleanBinaryStrategy;
import Runtime.Evaluate.Strategies.NumericBinaryStrategy;
import Runtime.Evaluate.Strategies.StringBinaryStrategy;
import Runtime.Interpreter;
import Runtime.Environment;

public abstract class BinaryExprFactory
{
    public static BinaryExprStrategy build(BinaryExpr expr, Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue left = Interpreter.evaluate(expr.left, env);
        RuntimeValue right = Interpreter.evaluate(expr.right, env);
        String operator = expr.operator;

        if (NumericBinaryStrategy.canEvaluate(right, left, operator))
        {
            return new NumericBinaryStrategy();
        }

        if (StringBinaryStrategy.canEvaluate(right, left, operator))
        {
            return new StringBinaryStrategy();
        }

        if (BooleanBinaryStrategy.canEvaluate(operator))
        {
            return new BooleanBinaryStrategy();
        }

        throw new InvalidBinaryOperation("Operação binária informada ainda não é suportada.");
    }
}
