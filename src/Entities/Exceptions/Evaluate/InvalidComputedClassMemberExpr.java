package Entities.Exceptions.Evaluate;

import Ast.Expressions.MemberExpr;

public class InvalidComputedClassMemberExpr extends RuntimeException
{
    public InvalidComputedClassMemberExpr()
    {
        super("Não é possível chamar o membro de uma classe computada.");
    }

    public static void throwIfComputed(MemberExpr expr)
    {
        if (expr.computed)
        {
            throw new InvalidComputedClassMemberExpr();
        }
    }
}
