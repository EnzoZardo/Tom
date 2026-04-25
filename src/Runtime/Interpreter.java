package Runtime;

import Ast.Expressions.Literals.*;
import Ast.Statements.*;
import Ast.Expressions.*;
import Entities.Enums.Ast.NodeType;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Runtime.Evaluate.Expressions;
import Runtime.Evaluate.Statements;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.NumericValue;
import Runtime.Values.StringValue;

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
            case NodeType.IntegerLiteral -> NumericValue.create(((IntegerLiteral) node).value, true);
            case NodeType.FloatLiteral -> NumericValue.create(((FloatLiteral) node).value, false);
            case NodeType.StringLiteral -> StringValue.create(((StringLiteral) node).value);
            case NodeType.Identifier -> Expressions.evaluateIdentifier((Identifier) node, env);
            case NodeType.ArrayLiteral -> Expressions.evaluateArrayExpression((ArrayLiteral) node, env);
            case NodeType.ObjectLiteral -> Expressions.evaluateObjectExpression((ObjectLiteral) node, env);
            case NodeType.CallExpression -> Expressions.evaluateCallExpression((CallExpr) node, env);
            case NodeType.MemberExpression -> Expressions.evaluateMemberExpression((MemberExpr) node, env);
            case NodeType.AssignmentExpression -> Expressions.evaluateVariableAssignment((AssignmentExpr) node, env);
            case NodeType.UnaryExpr -> Expressions.evaluateUnaryExpr((UnaryExpr) node, env);
            case NodeType.BinaryExpr -> Expressions.evaluateBinaryExpr((BinaryExpr) node, env);
            case NodeType.Program -> Statements.evaluateProgram((Program) node, env);
            case NodeType.IfStatement -> Statements.evaluateIfStatement((IfStatement) node, env);
            case NodeType.WhileStatement -> Statements.evaluateWhileStatement((WhileStatement) node, env);
            case NodeType.VariableDeclaration ->
                    Statements.evaluateVariableDeclaration((VariableDeclaration) node, env);
            case NodeType.FunctionDeclaration ->
                    Statements.evaluateFunctionDeclaration((FunctionDeclaration) node, env);
            case NodeType.TypeDeclaration ->
                    Statements.evaluateTypeDeclaration((TypeDeclaration) node, env);
            case NodeType.ScopeDeclaration ->
                    Statements.evaluateScopeDeclaration((ScopeDeclaration) node, env);
            default -> throw new RuntimeException("This AST Node was not recognized yet." + node.print(0));
        };
    }
}
