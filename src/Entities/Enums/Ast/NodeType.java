package Entities.Enums.Ast;

public enum NodeType
{
    // These are statements
    Program,
    IfStatement,
    WhileStatement,
    TypeDeclaration,
    ScopeDeclaration,
    ForEachStatement,
    VariableDeclaration,
    FunctionDeclaration,

    // These are expressions
    UnaryExpr,
    BinaryExpr,
    CallExpression,
    MemberExpression,
    AssignmentExpression,

    // These are literals
    Property,
    FloatLiteral,
    Identifier,
    ArrayLiteral,
    StringLiteral,
    IntegerLiteral,
    ObjectLiteral,
}
