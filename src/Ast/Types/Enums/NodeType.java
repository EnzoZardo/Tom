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

    // These are literals
    Property,
    ObjectLiteral,
    IntegerValue,
    FloatValue,
    Identifier,
    BinaryExpr,
}
