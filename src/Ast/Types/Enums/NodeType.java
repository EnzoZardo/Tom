package Ast.Types.Enums;

public enum NodeType
{
    // These are statements
    Program,
    VariableDeclaration,
    FunctionDeclaration,

    // These are expressions
    AssignmentExpression,

    // These are literals
    Property,
    ObjectLiteral,
    NumericLiteral,
    Identifier,
    BinaryExpr,
}
