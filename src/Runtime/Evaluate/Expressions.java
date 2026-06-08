package Runtime.Evaluate;

import Ast.Expressions.*;
import Ast.Expressions.Literals.ArrayLiteral;
import Ast.Expressions.Literals.ClassLiteral;
import Entities.Abstractions.Evaluate.Strategies.BinaryExprStrategy;
import Entities.Abstractions.Evaluate.Strategies.UnaryExprStrategy;
import Entities.Abstractions.Type;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Ast.Expressions.Literals.ObjectLiteral;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Ast.Statement;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.*;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Exceptions.InvalidCallException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Entities.Exceptions.Parser.InvalidStatementContextException;
import Entities.Metadata.ParameterMetadata;
import Runtime.Environment;
import Runtime.Evaluate.Factory.BinaryExpr.BinaryExprFactory;
import Runtime.Evaluate.Factory.UnaryExpr.UnaryExprFactory;
import Runtime.Interpreter;
import Entities.Enums.Runtime.ValueType;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.*;
import Runtime.TypeChecker;
import Entities.Metadata.ArgumentMetadata;
import Runtime.Values.ClassValue;
import Runtime.Values.FlowControl.ReturnFlow;


import java.util.ArrayList;

public abstract class Expressions {
    public static RuntimeValue evaluateIdentifier(Identifier identifier, Environment env) {
        return env.lookupVariable(identifier.value);
    }

    public static RuntimeValue evaluateUnaryExpr(UnaryExpr expr, Environment env)
        throws AlreadyDeclaredVariableException {
        ErrorOr<UnaryExprStrategy> result = UnaryExprFactory.build(expr);

        if (result.isError()) {
            throw new InvalidUnaryExpression(result.error.getMessage());
        }

        RuntimeValue right = Interpreter.evaluate(expr.right, env);
        UnaryExprStrategy strategy = result.value;

        return strategy.evaluate(right, expr.operator);
    }

    public static RuntimeValue evaluateBinaryExpr(BinaryExpr expr, Environment env)
        throws AlreadyDeclaredVariableException {
        ErrorOr<BinaryExprStrategy> result = BinaryExprFactory.build(expr, env);

        if (result.isError())
        {
            throw new InvalidBinaryOperation(result.error.getMessage());
        }

        BinaryExprStrategy strategy = result.value;
        RuntimeValue left = Interpreter.evaluate(expr.left, env);
        RuntimeValue right = Interpreter.evaluate(expr.right, env);

        return strategy.evaluate(left, right, expr.operator);
    }

    public static RuntimeValue evaluateVariableAssignment(AssignmentExpr assignment, Environment env)
        throws AlreadyDeclaredVariableException {
        if (assignment.assigned.type == NodeType.Identifier)
        {
            String name = ((Identifier) assignment.assigned).value;
            RuntimeValue value = Interpreter.evaluate(assignment.value, env);
            return env.assignVariable(name, value);
        }

        if (assignment.assigned.type == NodeType.MemberExpression)
        {
            MemberExpr memberExpr = ((MemberExpr) assignment.assigned);
            RuntimeValue value = Interpreter.evaluate(assignment.value, env);

            if (memberExpr.object.type == NodeType.Identifier)
            {
                Identifier objectIdentifier = (Identifier) memberExpr.object;
                RuntimeValue variable = env.lookupVariable(objectIdentifier.value);

//                if (!memberExpr.computed && variable.type == ValueType.Class)
//                {
//                    Identifier memberIdentifier = (Identifier) memberExpr.property;
//                    return env.assignClassMember(objectIdentifier.value, memberIdentifier.value, value);
//                }

//                if (!memberExpr.computed && variable.type == ValueType.Object)
//                {
//                    Identifier memberIdentifier = (Identifier) memberExpr.property;
//                    return env.assignMember(objectIdentifier.value, memberIdentifier.value, value);
//                }

                RuntimeValue propValue = Interpreter.evaluate(memberExpr.property, env);

                if (propValue.type == ValueType.String && variable.type == ValueType.Object)
                {
                    StringValue memberIdentifier = (StringValue) propValue;
                    return env.assignMember(objectIdentifier.value, memberIdentifier.value, value);
                }

                if (propValue.type == ValueType.Numeric && variable.type == ValueType.Array)
                {
                    NumericValue memberIdentifier = (NumericValue) propValue;

                    if (!memberIdentifier.isInteger)
                    {
                        throw new ExpectedTypeNotMatch("Não se pode indexar uma lista com uma chave do tipo real.");
                    }

                    return env.assignIndex(objectIdentifier.value, (int) memberIdentifier.value, value);
                }
            }

            if (memberExpr.object.type == NodeType.ObjectLiteral || memberExpr.object.type == NodeType.ArrayLiteral)
            {
                return value;
            }
        }

        throw new InvalidNodeException("Esperávamos um membro de objeto ou uma variável para a expressão " +
            "darmos um novo valor para ela.");
    }

    public static RuntimeValue evaluateObjectExpression(ObjectLiteral object, Environment env)
        throws AlreadyDeclaredVariableException {
        ObjectValue value = ObjectValue.create();

        for (Property prop : object.properties)
        {
            if (prop.value != null)
            {
                value.properties.put(prop.key, Interpreter.evaluate(prop.value, env));
                continue;
            }

            value.properties.put(prop.key, env.lookupVariable(prop.key));
        }

        return value;
    }

    public static RuntimeValue evaluateArrayExpression(ArrayLiteral array, Environment env)
        throws AlreadyDeclaredVariableException {
        ArrayValue value = ArrayValue.create();

        for (int i = 0; i < array.items.size(); i++) {
            Expr item = array.items.get(i);
            RuntimeValue evaluated = Interpreter.evaluate(item, env);
            value.items.put(i, evaluated);
        }

        return value;
    }


    public static RuntimeValue evaluateMemberExpression(MemberExpr memberExpr, Environment env)
        throws AlreadyDeclaredVariableException {
        RuntimeValue entity = Interpreter.evaluate(memberExpr.object, env);

        if (entity.type == ValueType.Class) {
            ClassValue value = (ClassValue) entity;

            if (memberExpr.computed) {
                //TODO: change this
                throw new InvalidArrayIndexTypeException();
            }

            if (memberExpr.property.type == NodeType.Identifier) {
                Identifier identifier = (Identifier) memberExpr.property;
                ClassMemberValue member = value.members.get(identifier.value);
                ClassMemberValue bound = (ClassMemberValue) member.copy();

                bound.owner = value;
                return bound;
            }

            return NullValue.create();
        }

        if (entity.type == ValueType.Object) {
            ObjectValue value = (ObjectValue) entity;

            if (memberExpr.property.type == NodeType.Identifier && !memberExpr.computed) {
                Identifier id = (Identifier) memberExpr.property;

                if (value.properties.containsKey(id.value)) {
                    return value.properties.get(id.value);
                }
            } else {
                RuntimeValue member = Interpreter.evaluate(memberExpr.property, env);

                if (member.type == ValueType.String) {
                    StringValue id = (StringValue) member;

                    if (value.properties.containsKey(id.value)) {
                        return value.properties.get(id.value);
                    }
                }

                throw new InvalidComputedObjectKeyType();
            }

            return NullValue.create();
        }

        if (entity.type == ValueType.Array && memberExpr.computed)
        {
            ArrayValue value = (ArrayValue) entity;

            RuntimeValue member = Interpreter.evaluate(memberExpr.property, env);

            if (member.type == ValueType.Numeric)
            {
                NumericValue key = (NumericValue) member;

                if (!key.isInteger || key.value < 0)
                {
                    throw new InvalidArrayIndexTypeException();
                }

                int index = (int)key.value;
                if (value.items.containsKey(index))
                {
                    return value.items.get(index);
                }

                return NullValue.create();
            }

            throw new InvalidArrayIndexTypeException();
        }

        if (entity.type == ValueType.String && memberExpr.computed)
        {
            StringValue value = (StringValue) entity;

            RuntimeValue member = Interpreter.evaluate(memberExpr.property, env);

            if (member.type == ValueType.Numeric)
            {
                NumericValue key = (NumericValue) member;

                if (!key.isInteger || key.value < 0)
                {
                    throw new InvalidArrayIndexTypeException();
                }

                int index = (int)key.value;
                if (index < value.value.length())
                {
                    return StringValue.create(Character.toString(value.value.charAt(index)));
                }

                return NullValue.create();
            }

            throw new InvalidArrayIndexTypeException();
        }

        throw new InvalidNodeException("Esperávamos um objeto ou lista para buscarmos uma chave dele.");
    }

    public static RuntimeValue evaluateInstantiationExpression(
        ClassLiteral classLiteral, Environment env)
        throws AlreadyDeclaredVariableException {
        Environment declarationEnv = env.resolve(classLiteral.className);
        RuntimeValue declarationValue = declarationEnv.lookupVariable(classLiteral.className);

        if (declarationValue.type != ValueType.Class) {
            throw new InvalidNodeException("Esperávamos um o nome de uma classe para instanciar.");
        }

        ClassValue value = ((ClassValue) declarationValue).copy();

        if (!value.members.containsKey(classLiteral.className)) {
            if (classLiteral.arguments.isEmpty()) {
                return value;
            }

            throw new InvalidCallException("Não foi encontrado nenhum construtor " +
                    "com esse número de argumentos para esta classe.");
        }

        ArrayList<RuntimeValue> args = new ArrayList<>();

        for (Expr expr : classLiteral.arguments) {
            args.add(Interpreter.evaluate(expr, env));
        }

        ClassMemberValue constructor = value.members.get(classLiteral.className);

        if (constructor.value.type != ValueType.Function) {
            throw new InvalidCallException("Valor informado não permite ser chamado como um construtor.");
        }

        FunctionValue function = (FunctionValue) constructor.value;
        Environment scope = Environment.create(function.declarationEnv);

        if (function.parameters.size() != classLiteral.arguments.size()) {
            throw new IncorrectNumberOfArgumentsException(String.format(
                    "A função %s esperava %d argumento(s), mas recebeu %d.",
                    function.name,
                    function.parameters.size(),
                    classLiteral.arguments.size()));
        }

        Environment typeEnv = env.resolveType(value.className);
        Type type = typeEnv.lookupType(value.className);

        scope.declareVariable(ReservedKeys.This, value, type, false);

        for (int i = 0; i < function.parameters.size(); i++) {
            ArgumentMetadata param = function.parameters.get(i);
            String name = param.getName();

            ErrorOr<Void> equality = TypeChecker.check(env, args.get(i), param.getType());
            if (equality.isError()) {
                throw new RuntimeException(String.format(
                        "Tipo incorreto informado para o argumento '%s'. %s",
                        name,
                        equality.error.getMessage()));
            }

            scope.declareVariable(name, args.get(i), param.getType(), false);
        }

        RuntimeValue result;
        for (Statement statement : function.body)
        {
            result = Interpreter.evaluate(statement, scope);

            if (result.type == ValueType.Return)
            {
                throw new InvalidStatementContextException("Não se pode haver um retorno em um construtor.");
            }
        }

        return value;
    }

    public static RuntimeValue evaluateCallExpression(
        CallExpr call, Environment env) throws AlreadyDeclaredVariableException
    {
        ArrayList<RuntimeValue> args = new ArrayList<>();

        for (Expr expr : call.arguments)
        {
            args.add(Interpreter.evaluate(expr, env));
        }

        RuntimeValue caller = Interpreter.evaluate(call.caller, env);

        if (caller.type == ValueType.ClassMember)
        {
            ClassMemberValue member = (ClassMemberValue) caller;
            if (member.value.type != ValueType.Function)
            {
                throw new InvalidCallException("Valor informado não permite ser chamado como uma função.");
            }

            FunctionValue function = (FunctionValue) member.value;
            //TODO: discover if this works, function has declarationenv
            Environment scope = Environment.create(function.declarationEnv);

            if (function.parameters.size() != call.arguments.size())
            {
                throw new IncorrectNumberOfArgumentsException(String.format(
                        "A função %s esperava %d argumento(s), mas recebeu %d.",
                        function.name,
                        function.parameters.size(),
                        call.arguments.size()));
            }

            for (int i = 0; i < function.parameters.size(); i++)
            {
                ArgumentMetadata param = function.parameters.get(i);
                String name = param.getName();

                ErrorOr<Void> equality = TypeChecker.check(env, args.get(i), param.getType());
                if (equality.isError()) {
                    throw new RuntimeException(String.format(
                            "Tipo incorreto informado para o argumento '%s'. %s",
                            name,
                            equality.error.getMessage()));
                }

                scope.declareVariable(name, args.get(i), param.getType(), false);
            }

            Environment typeEnv = env.resolveType(member.owner.className);
            Type type = typeEnv.lookupType(member.owner.className);

            scope.declareVariable(
                ReservedKeys.This,
                member.owner,
                type,
                false);

            RuntimeValue result = NullValue.create();
            for (Statement statement : function.body)
            {
                result = Interpreter.evaluate(statement, scope);

                if (result.type == ValueType.Return)
                {
                    break;
                }
            }

            RuntimeValue ret = result.type == ValueType.Return
                    ? ((ReturnFlow) result).value
                    : NullValue.create();

            if (ret.type == ValueType.ClassMember)
            {
                //TODO: ver um jeito de tirar isso
                assert ret instanceof ClassMemberValue;
                ret = ((ClassMemberValue) ret).value;
            }

            ErrorOr<Void> equality = TypeChecker.check(env, ret, function.returnType);

            if (equality.isError()) {
                throw new ExpectedTypeNotMatch(String.format(
                        "Tipo de retorno não condiz com o tipo esperado. %s",
                        equality.error.getMessage()));
            }

            return ret;
        }

        if (caller.type == ValueType.Function)
        {
            FunctionValue function = (FunctionValue) caller;
            Environment scope = Environment.create(function.declarationEnv);

            if (function.parameters.size() != call.arguments.size())
            {
                throw new IncorrectNumberOfArgumentsException(String.format(
                    "A função %s esperava %d argumento(s), mas recebeu %d.",
                    function.name,
                    function.parameters.size(),
                    call.arguments.size()));
            }

            for (int i = 0; i < function.parameters.size(); i++)
            {
                ArgumentMetadata param = function.parameters.get(i);
                String name = param.getName();

                ErrorOr<Void> equality = TypeChecker.check(env, args.get(i), param.getType());
                if (equality.isError()) {
                    throw new RuntimeException(String.format(
                        "Tipo incorreto informado para o argumento '%s'. %s",
                        name,
                        equality.error.getMessage()));
                }

                scope.declareVariable(name, args.get(i), param.getType(), false);
            }

            RuntimeValue result = NullValue.create();
            for (Statement statement : function.body)
            {
                result = Interpreter.evaluate(statement, scope);

                if (result.type == ValueType.Return)
                {
                    break;
                }
            }

            RuntimeValue ret = result.type == ValueType.Return
                ? ((ReturnFlow) result).value
                : NullValue.create();

            ErrorOr<Void> equality = TypeChecker.check(env, ret, function.returnType);

            if (equality.isError()) {
                throw new ExpectedTypeNotMatch(String.format(
                    "Tipo de retorno não condiz com o tipo esperado. %s",
                    equality.error.getMessage()));
            }

            return ret;
        }

        if (caller.type == ValueType.NativeFunction) {
            NativeFunctionValue nativeFunction = (NativeFunctionValue) caller;
            ParameterMetadata parameters = ParameterMetadata.create(args, env);
            return nativeFunction.call.apply(parameters);
        }

        throw new InvalidCallException("Valor informado não permite ser chamado como uma função.");
    }
}
