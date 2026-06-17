package Runtime;

import Ast.Expressions.Literals.*;
import Ast.Statements.*;
import Ast.Expressions.*;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.InvalidArgumentException;
import Runtime.Evaluate.Expressions;
import Runtime.Evaluate.Statements;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.NumericValue;
import Runtime.Values.StringValue;

import java.io.IOException;

public abstract class Interpreter
{
    public static RuntimeValue evaluate(Statement node, Environment env)
        throws AlreadyDeclaredVariableException
    {
        return switch (node.type) {
            case IntegerLiteral -> NumericValue.create(((IntegerLiteral) node).value, true);
            case FloatLiteral -> NumericValue.create(((FloatLiteral) node).value, false);
            case StringLiteral -> StringValue.create(((StringLiteral) node).value);
            case Identifier -> Expressions.evaluateIdentifier((Identifier) node, env);
            case ArrayLiteral -> Expressions.evaluateArrayExpression((ArrayLiteral) node, env);
            case ObjectLiteral -> Expressions.evaluateObjectExpression((ObjectLiteral) node, env);
            case ClassLiteral -> Expressions.evaluateInstantiationExpression((ClassLiteral) node, env);
            case CallExpression -> Expressions.evaluateCallExpression((CallExpr) node, env);
            case MemberExpression -> Expressions.evaluateMemberExpression((MemberExpr) node, env);
            case AssignmentExpression -> Expressions.evaluateVariableAssignment((AssignmentExpr) node, env);
            case UnaryExpr -> Expressions.evaluateUnaryExpr((UnaryExpr) node, env);
            case BinaryExpr -> Expressions.evaluateBinaryExpr((BinaryExpr) node, env);
            case Program -> Statements.evaluateProgram((Program) node, env);
            case IfStatement -> Statements.evaluateIfStatement((IfConditional) node, env);
            case WhileStatement -> Statements.evaluateWhileStatement((While) node, env);
            case ForEachStatement -> Statements.evaluateForEachStatement((ForEach) node, env);
            case VariableDeclaration -> Statements.evaluateVariableDeclaration((VariableDeclaration) node, env);
            case FunctionDeclaration -> Statements.evaluateFunctionDeclaration((FunctionDeclaration) node, env);
            case TypeDeclaration -> Statements.evaluateTypeDeclaration((TypeDeclaration) node, env);
            case ScopeDeclaration -> Statements.evaluateScopeDeclaration((ScopeDeclaration) node, env);
            case ClassDeclaration -> Statements.evaluateClassDeclaration((ClassDeclaration) node, env);
            case Return -> Statements.evaluateReturnStatement((Return) node, env);
            case Continue -> Statements.evaluateContinue();
            case Break -> Statements.evaluateBreak();
            case Import -> Statements.evaluateImport((Import) node, env);
            default -> throw new RuntimeException("This AST Node was not recognized yet." + node.print(0));
        };
    }
}
