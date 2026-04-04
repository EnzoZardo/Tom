package Runtime;

import Ast.Statements.*;
import Ast.Types.Enums.NodeType;
import Ast.Types.Statement;
import Exceptions.AlreadyDeclaredVariableException;
import Runtime.Evaluate.Expressions;
import Runtime.Evaluate.Statements;
import Runtime.Types.RuntimeValue;
import Runtime.Values.NumericValue;

public class Interpreter
{
    private Interpreter()
    {
    }

    public static Interpreter create()
    {
        return new Interpreter();
    }

    public static RuntimeValue evaluate(Statement node, Environment env) throws AlreadyDeclaredVariableException
    {
        return switch (node.type)
        {
            case NodeType.IntegerValue -> NumericValue.create(((IntegerLiteral) node).number, true);
            case NodeType.FloatValue -> NumericValue.create(((FloatLiteral) node).number, false);
            case NodeType.Identifier -> Expressions.evaluateIdentifier((Identifier) node, env);
            case NodeType.ObjectLiteral -> Expressions.evaluateObjectExpression((ObjectLiteral) node, env);
            case NodeType.CallExpression -> Expressions.evaluateCallExpression((CallExpr) node, env);
            case NodeType.MemberExpression -> Expressions.evaluateMemberExpression((MemberExpr) node, env);
            case NodeType.AssignmentExpression -> Expressions.evaluateVariableAssignment((AssignmentExpr) node, env);
            case NodeType.BinaryExpr -> Expressions.evaluateBinaryExpr((BinaryExpr) node, env);
            case NodeType.Program -> Statements.evaluateProgram((Program) node, env);
            case NodeType.VariableDeclaration ->
                    Statements.evaluateVariableDeclaration((VariableDeclaration) node, env);
            case NodeType.FunctionDeclaration ->
                    Statements.evaluateFunctionDeclaration((FunctionDeclaration) node, env);
            default -> throw new RuntimeException("This AST Node was not recognized yet." + node.print(0));
        };
    }
}
