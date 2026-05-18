package Entities.Enums.Ast;

public enum NodeType
{
    /* #region Statements */
    Break,
    Return,
    Program,
    Continue,
    IfStatement,
    WhileStatement,
    TypeDeclaration,
    ClassDeclaration,
    ScopeDeclaration,
    ForEachStatement,
    VariableDeclaration,
    FunctionDeclaration,
    ClassMemberDeclaration,
    /* #endregion */

    /* #region Expressions */
    UnaryExpr,
    BinaryExpr,
    CallExpression,
    MemberExpression,
    AssignmentExpression,
    /* #endregion */

    /* #region Literals */
    Property,
    Identifier,
    FloatLiteral,
    ArrayLiteral,
    StringLiteral,
    ObjectLiteral,
    IntegerLiteral,
    /* #endregion */
}
