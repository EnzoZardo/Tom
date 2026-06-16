package Runtime.Evaluate.Strategies.AssignmentExpr;

import Ast.Expressions.AssignmentExpr;
import Ast.Expressions.Identifier;
import Entities.Abstractions.Evaluate.Strategies.AssignmentExprStrategy;
import Entities.Abstractions.Runtime.RuntimeValue;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Environment;
import Runtime.Interpreter;

public class IdentifierAssignmentStrategy implements AssignmentExprStrategy
{
    @Override
    public RuntimeValue evaluate(AssignmentExpr assignment, Environment environment)
        throws AlreadyDeclaredVariableException
    {
        Identifier identifier = (Identifier) assignment.assigned;
        RuntimeValue value = Interpreter.evaluate(assignment.value, environment);
        return environment.assignVariable(identifier.value, value);
    }
}
