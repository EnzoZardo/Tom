package Ast.Enums;

public enum NodeType
{
    // These are statements
    Program,
    VariableDeclaration,
    FunctionDeclaration,
    TypeDeclaration,

    // These are expressions
    AssignmentExpression,
    MemberExpression,
    CallExpression,
    BinaryExpr,
    UnaryExpr,

    // These are literals
    Property,
    ObjectLiteral,
    IntegerValue,
    FloatValue,
    StringValue,
    Identifier,
}
