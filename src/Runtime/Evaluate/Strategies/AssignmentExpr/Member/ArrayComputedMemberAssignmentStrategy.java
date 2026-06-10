package Runtime.Evaluate.Strategies.AssignmentExpr.Member;

import Ast.Expressions.Identifier;
import Ast.Expressions.MemberExpr;
import Entities.Abstractions.Evaluate.Strategies.MemberAssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Runtime.ValueType;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Values.NumericValue;
import Runtime.Values.StringValue;
import Runtime.Environment;
import Runtime.Interpreter;

public class ArrayComputedMemberAssignmentStrategy implements MemberAssignmentExprStrategy
{
    public static boolean canEvaluate(MemberExpr member, RuntimeValue variable, RuntimeValue property)
    {
        if (property.type != ValueType.Numeric)
        {
            return false;
        }

        NumericValue value = (NumericValue) property;

        return member.computed
            && value.isInteger
            && variable.type == ValueType.Array
            && member.object.type == NodeType.Identifier;
    }

    @Override
    public RuntimeValue evaluate(MemberExpr member, RuntimeValue value, Environment env) throws AlreadyDeclaredVariableException
    {
        Identifier objectIdentifier = (Identifier) member.object;

        RuntimeValue memberIdentifier = Interpreter.evaluate(member.property, env);
        NumericValue computedProperty = (NumericValue) memberIdentifier;

        return env.assignIndex(objectIdentifier.value, (int) computedProperty.value, value);
    }
}

