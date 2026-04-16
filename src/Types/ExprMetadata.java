package Types;

import Ast.Statements.Expressions.Expr;
import Ast.Statements.Types.Type;

public class ExprMetadata extends Pair<Type, Expr>
{
    protected ExprMetadata(Type type, Expr expr)
    {
        super(type, expr);
    }

    public static ExprMetadata create(Type type, Expr expr)
    {
        return new ExprMetadata(type, expr);
    }

    public Type getType()
    {
        return get0();
    }

    public Expr getExpr()
    {
        return get1();
    }
}