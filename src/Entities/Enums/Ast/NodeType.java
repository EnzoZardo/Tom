package Entities.Enums.Ast;

public enum NodeType
{
    // These are statements
    Program,
    TypeDeclaration,
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
    FloatValue,
    Identifier,
    StringValue,
    IntegerValue,
    ObjectLiteral,
}
