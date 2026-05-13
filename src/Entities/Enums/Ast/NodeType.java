package Entities.Enums.Ast;

public enum NodeType
{
    // These are statements
    Break,
    Return,
    Program,
    Continue,
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
