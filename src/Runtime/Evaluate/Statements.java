package Runtime.Evaluate;

import Ast.Expressions.Identifier;
import Ast.Expressions.Literals.ClassLiteral;
import Ast.Statements.*;
import Ast.Types.ClassType;
import Ast.Types.GenericType;
import Ast.Types.Primitive.IntegerType;
import Ast.Types.SymbolType;
import Entities.Abstractions.Runtime.RuntimeException;
import Entities.Abstractions.Ast.Expr;
import Entities.Abstractions.Type;
import Entities.Abstractions.Ast.Statement;
import Entities.Common.Result.ErrorOr;
import Entities.Constants.ReservedKeys;
import Entities.Enums.Ast.NodeType;
import Entities.Enums.Runtime.ValueType;
import Entities.Enums.TypeKind;
import Entities.Exceptions.AlreadyDeclaredVariableException;
import Entities.Exceptions.Evaluate.IncorrectNumberOfArgumentsException;
import Entities.Exceptions.Evaluate.InvalidBinaryOperation;
import Entities.Exceptions.ExpectedTypeNotMatch;
import Entities.Exceptions.FileNotExistsException;
import Entities.Exceptions.ImportInvalidExtension;
import Entities.Exceptions.Parser.AlreadyImportedModuleException;
import Entities.Exceptions.Parser.ConstructorNeededException;
import Entities.Exceptions.Parser.InvalidNodeException;
import Runtime.Environment;
import Runtime.Interpreter;
import Entities.Abstractions.Runtime.RuntimeValue;
import Runtime.Values.*;
import Runtime.TypeChecker;
import Runtime.Values.ClassValue;
import Runtime.Values.FlowControl.BreakFlow;
import Runtime.Values.FlowControl.ContinueFlow;
import Runtime.Values.FlowControl.ReturnFlow;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Statements
{
    private static final String EXTENSION = "tom";
    public static RuntimeValue evaluateProgram(Program program, Environment env)
        throws AlreadyDeclaredVariableException
    {
        RuntimeValue lastEvaluated = NullValue.create();

        for (Statement stmt : program.body) lastEvaluated = Interpreter.evaluate(stmt, env);

        return lastEvaluated;
    }

    private static String getExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public static RuntimeValue evaluateImport(Import importStatement, Environment env)
            throws AlreadyDeclaredVariableException
    {
        RuntimeValue value = Interpreter.evaluate(importStatement.path, env);

        if (value.type != ValueType.String)
            throw new InvalidNodeException("Só podemos importar valores textuais.");

        StringValue path = (StringValue) value;
        String content;


        File tmp = new File(path.value);
        String extension = getExtension(tmp.getName());

        if (!extension.isBlank() && !extension.equals(EXTENSION))
            throw new ImportInvalidExtension("Extensão - " + extension + " - inválida para ser importada");

        if (extension.isBlank())
            path.value += '.' + EXTENSION;

        File file = new File(path.value);

        if (!file.exists()) throw new FileNotExistsException("Arquivo - " + file.getName() + " - para a importação inexistente");

        try (FileReader reader = new FileReader(file))
        {
            content = reader.readAllAsString();
        }
        catch (IOException e)
        {
            return NullValue.create();
        }

        if (!env.canImport(file.getAbsolutePath()))
            throw new AlreadyImportedModuleException("Módulo - " + path.value + " - já importado neste escopo.");

        env.addImport(file.getAbsolutePath());
        Program program = Program.initialize(content, path.value);
        return Interpreter.evaluate(program, env);
    }

    public static RuntimeValue evaluateVariableDeclaration(
        VariableDeclaration declaration, Environment env) throws AlreadyDeclaredVariableException {
        ClassType hint = extractClassType(env, declaration.expectedType);
        RuntimeValue value = declaration.value == null
                ? EmptyValue.create()
                : evaluateValue(declaration.value, env, hint);

        ErrorOr<Void> equality = TypeChecker.check(env, value, declaration.expectedType);
        if (equality.isError() && declaration.value != null)
            throw new ExpectedTypeNotMatch(String.format("Tipo incorreto informado para a variável '%s'. %s",
                declaration.identifier,
                equality.error.getMessage()));

        return env.declareVariable(declaration.identifier, value, declaration.expectedType, declaration.constant);
    }

    private static ClassType extractClassType(Environment env, Type expectedType)
    {
        if (expectedType == null || expectedType.type != TypeKind.SymbolType)
            return null;

        SymbolType symbol = (SymbolType) expectedType;
        if (symbol.parameters.isEmpty())
            return null;

        try
        {
            Type reduced = Type.reduce(env, expectedType);
            if (reduced.type == TypeKind.ClassType)
                return (ClassType) reduced;
        }
        catch (Exception ignored)
        {
        }

        return null;
    }

    private static RuntimeValue evaluateValue(Expr value, Environment env, ClassType hint)
        throws AlreadyDeclaredVariableException
    {
        if (hint != null && value.type == NodeType.ClassLiteral)
        {
            ClassLiteral literal = (ClassLiteral) value;
            if (literal.typeArguments.isEmpty())
                return Expressions.evaluateInstantiationExpression(literal, env, hint);
        }

        return Interpreter.evaluate(value, env);
    }

    public static RuntimeValue evaluateClassDeclaration(
        ClassDeclaration declaration, Environment env) throws AlreadyDeclaredVariableException
    {
        HashMap<String, ClassMemberValue> members = new HashMap<>();
        ClassValue classValue;

        if (declaration.parentClass != null)
        {
            Environment parentEnv = env.resolve(declaration.parentClass);
            RuntimeValue value = parentEnv.lookupVariable(declaration.parentClass);

            if (value.type != ValueType.Class)
                throw new ExpectedTypeNotMatch("Classes somente podem estender de outras classes");

            ClassValue parent = ((ClassValue) value).copy();
            classValue = ClassValue.create(
                declaration.name, parent, members, true, declaration.isAbstract,
                new ArrayList<>(declaration.typeParameters));

            if (declaration.parentType != null)
                classValue.setParentTypeArguments(new ArrayList<>(((SymbolType) declaration.parentType).parameters));
        } else { 
            classValue = ClassValue.create(
                declaration.name, members, true, declaration.isAbstract,
                new ArrayList<>(declaration.typeParameters));
        }

        Environment classEnv = Environment.create(env);

        for (String typeParameter : declaration.typeParameters)
        {
            classEnv.declareType(typeParameter, GenericType.create(typeParameter));
        }

        for (ClassMemberDeclaration member : declaration.members)
        {
            RuntimeValue value = Interpreter.evaluate(member.consequent, classEnv);
            members.put(
                member.getMemberName(),
                ClassMemberValue.create(
                    member.protectionMarker,
                    value,
                    classValue,
                    member.getMemberType(),
                    member.isStatic)
            );
        }

        if (members.containsKey(declaration.name)
            && members.get(declaration.name).type.type != TypeKind.FunctionType)
            throw new ExpectedTypeNotMatch("Propriedade com o nome da classe inválido.");

        if (declaration.parentClass != null && !members.containsKey(declaration.name))
            throw new ConstructorNeededException("É necessário um construtor que chame a " +
                "classe pai em classes com heranças");

        ClassType type = ClassType.create(declaration.name);

        return env.declareClass(declaration, classValue, type);
    }

    public static RuntimeValue evaluateTypeDeclaration(
        TypeDeclaration declaration, Environment env)
    {
        if (declaration.typeParameters.isEmpty())
        {
            env.declareType(declaration.identifier, Type.reduce(env, declaration.value));
        }
        else
        {
            env.declareType(declaration.identifier, declaration.value, declaration.typeParameters);
        }
        return NullValue.create();
    }

    public static RuntimeValue evaluateFunctionDeclaration(
        FunctionDeclaration declaration, Environment env) throws AlreadyDeclaredVariableException
    {
        FunctionValue value = FunctionValue.createFromStatement(declaration, env);
        return env.declareConstant(value.name, value, value.type());
    }

    public static RuntimeException evaluateReturnStatement(
            Return returnStatement, Environment env) throws AlreadyDeclaredVariableException
    {
        if (returnStatement.value == null) return ReturnFlow.create(EmptyValue.create());
        return ReturnFlow.create(Interpreter.evaluate(returnStatement.value, env));
    }

    public static RuntimeValue evaluateScopeDeclaration(
        ScopeDeclaration scopeDeclaration,
        Environment env) throws AlreadyDeclaredVariableException
    {
        Environment scope = Environment.create(env);
        for (Statement statement : scopeDeclaration.body)
        {
            RuntimeValue value = Interpreter.evaluate(statement, scope);

            if (value.type == ValueType.Return ||
                value.type == ValueType.Continue ||
                value.type == ValueType.Break)
            {
                return value;
            }
        }

        return NullValue.create();
    }

    public static RuntimeValue evaluateIfStatement(
        IfConditional ifStatement,
        Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue value = Interpreter.evaluate(ifStatement.test, env);

        if (value.bool())
        {
            return Interpreter.evaluate(ifStatement.consequent, env);
        }

        if (ifStatement.alternate != null)
        {
            return Interpreter.evaluate(ifStatement.alternate, env);
        }

        return NullValue.create();
    }

    public static RuntimeValue evaluateWhileStatement(
        While whileStatement,
        Environment env) throws AlreadyDeclaredVariableException
    {
        RuntimeValue value = Interpreter.evaluate(whileStatement.test, env);
        RuntimeValue ret;
        while (value.bool())
        {
            ret = Interpreter.evaluate(whileStatement.consequent, env);

            if (ret.type == ValueType.Return)
                return ret;

            if (ret.type == ValueType.Break)
                return EmptyValue.create();

            value = Interpreter.evaluate(whileStatement.test, env);
        }

        return NullValue.create();
    }

    public static RuntimeValue evaluateForEachStatement(
            ForEach forEach,
            Environment env) throws AlreadyDeclaredVariableException
    {
        final int OBJECT_ARGS_SIZE = 3;
        final int ARGS_SIZE = 2;
        final int MINIMUM_ARGS_SIZE = 1;
        final String message = "Número incorreto de argumentos para o loop";
        RuntimeValue value;

        if (!ReservedKeys.In.equals(forEach.operator))
            throw new InvalidBinaryOperation("Esperávamos o token 'em' para nosso loop para-cada.");

        if (forEach.iterators.stream().anyMatch(x -> x.type != NodeType.Identifier))
        {
            throw new InvalidNodeException("Somente identificadores são aceitos em loops para-cada.");
        }

        RuntimeValue iterable = Interpreter.evaluate(forEach.iterable, env);

        if (forEach.iterators.size() > ARGS_SIZE && iterable.type != ValueType.Object
            || forEach.iterators.size() > OBJECT_ARGS_SIZE)
        {
            throw new IncorrectNumberOfArgumentsException(message);
        }

        List<Identifier> identifiers = forEach.iterators
            .stream()
            .map(x -> (Identifier) x)
            .toList();

        for (int i = 0; i < iterable.iteratorSize(); i++)
        {
            Environment operationEnv = Environment.create(env);

            switch (identifiers.size())
            {
                case MINIMUM_ARGS_SIZE -> operationEnv.declareConstant(
                        identifiers.getFirst().value,
                        iterable.iterate(i),
                        IntegerType.create());
                case ARGS_SIZE ->
                {
                    operationEnv.declareConstant(
                        identifiers.getFirst().value,
                        NumericValue.create(i, true),
                        IntegerType.create());
                    operationEnv.declareConstant(
                        identifiers.get(1).value,
                        iterable.iterate(i),
                        IntegerType.create());
                }
                case OBJECT_ARGS_SIZE ->
                {
                    ArrayValue iterated = (ArrayValue) iterable.iterate(i);
                    HashMap<Integer, RuntimeValue> items = iterated.items;
                    operationEnv.declareConstant(
                        identifiers.getFirst().value,
                        NumericValue.create(i, true),
                        IntegerType.create());
                    operationEnv.declareConstant(
                        identifiers.get(1).value,
                        items.get(0),
                        IntegerType.create());
                    operationEnv.declareConstant(
                        identifiers.getLast().value,
                        items.get(1),
                        IntegerType.create());
                }
                default -> throw new IncorrectNumberOfArgumentsException(message);
            }

            value = Interpreter.evaluate(forEach.consequent, operationEnv);

            if (value.type == ValueType.Return)
            {
                return value;
            }

            if (value.type == ValueType.Break)
            {
                return NullValue.create();
            }
        }

        return NullValue.create();
    }

    public static RuntimeException evaluateContinue()
    {
        return ContinueFlow.create();
    }

    public static RuntimeException evaluateBreak()
    {
        return BreakFlow.create();
    }
}
