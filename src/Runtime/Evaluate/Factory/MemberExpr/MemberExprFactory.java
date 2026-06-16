package Runtime.Evaluate.Factory.MemberExpr;

import Entities.Abstractions.Evaluate.Strategies.MemberExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Ast.Expressions.MemberExpr;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Evaluate.Strategies.MemberExpr.*;

public class MemberExprFactory
{
    public static ErrorOr<MemberExprStrategy> build(MemberExpr expr, RuntimeValue owner)
        throws AlreadyDeclaredVariableException
    {
        if (owner.type == ValueType.Class)
            return ErrorOr.Success(new ClassMemberStrategy());

        if (owner.type == ValueType.Object)
        {
            if (expr.property.type == NodeType.Identifier && !expr.computed)
                return ErrorOr.Success(new ObjectIdentifierMemberStrategy());

            return ErrorOr.Success(new ObjectComputedMemberStrategy());
        }

        if (owner.type == ValueType.Array && expr.computed)
            return ErrorOr.Success(new ArrayIndexMemberStrategy());

        if (owner.type == ValueType.String && expr.computed)
            return ErrorOr.Success(new StringIndexMemberStrategy());

        return ErrorOr.Fail("Expressão de membro inválida.");
    }
}
