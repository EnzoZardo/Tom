package Ast.Types.Enums;

public enum NodeType
{
    // These are statements
    Program,
    VariableDeclaration,
    FunctionDeclaration,

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
