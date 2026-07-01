package Runtime.Evaluate.Strategies.MemberExpr;

import Ast.Expressions.Identifier;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Common.Result.ErrorOr;
import Entities.Enums.Ast.NodeType;
import Entities.Exceptions.Evaluate.InvalidComputedClassMemberExpr;
import Entities.Exceptions.Evaluate.InvalidMemberAssignException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.ClassValue;
import Runtime.Values.NullValue;
import Runtime.Environment;
import Runtime.AccessChecker;

import javax.naming.directory.InvalidAttributeIdentifierException;

public class ClassMemberStrategy implements MemberExprStrategy
{
    @Override
    public RuntimeValue evaluate(MemberExpr expr, RuntimeValue owner, Environment environment)
    {
        InvalidComputedClassMemberExpr.throwIfComputed(expr);
        ClassValue value = (ClassValue) owner;

        if (expr.property.type == NodeType.Identifier) {
            Identifier identifier = (Identifier) expr.property;

            if (!value.members.containsKey(identifier.value)
                && (value.parent == null || !value.parent.members.containsKey(identifier.value)))
                throw new InvalidNodeException("Classe não possui membro com esse nome");

            ClassMemberValue member = value.members.get(identifier.value);;
            if (member == null) member = value.parent.members.get(identifier.value);

            ClassMemberValue bound = (ClassMemberValue) member.copy();

            ErrorOr<Void> accessResult = AccessChecker.canAccess(bound, environment.currentClass, identifier.value);

            if (accessResult.isError()) throw new InvalidMemberAssignException(accessResult.error.getMessage());

            bound.owner = value;
            return bound;
        }

        // Todo: change this
        return NullValue.create();
    }
}
