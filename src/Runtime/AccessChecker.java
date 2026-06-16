package Runtime;

import Entities.Common.Result.ErrorOr;
import Entities.Enums.Runtime.ProtectionLevel;
import Runtime.Values.ClassMemberValue;
import Runtime.Values.ClassValue;

public abstract class AccessChecker
{
    public static ErrorOr<Void> isSameOrSubclass(ClassValue caller, ClassValue owner)
    {
        ClassValue current = caller;

        while (current != null)
        {
            if (current.className.equals(owner.className))
                return ErrorOr.Success();
            current = current.parent;
        }

        return ErrorOr.Fail("Não é possível acessar o membro informado.");
    }

    public static ErrorOr<Void> canAccess(ClassMemberValue member, ClassValue caller, String keyName)
    {
        if (member.isStatic && !member.owner.isInstance)
            return ErrorOr.Fail("Apenas é possível acessar membros estáticos diretamente da instância da classe.");

        if (!member.isStatic && member.owner.isInstance)
            return ErrorOr.Fail("Apenas é possível acessar membros em classes instanciadas.");

        if (member.protectionLevel == ProtectionLevel.Public)
            return ErrorOr.Success();

        if (caller == null)
            return ErrorOr.Fail("Não é possível acessar o membro '" + keyName + "' devido ao seu nível de proteção.");

        if (member.protectionLevel == ProtectionLevel.Private)
        {
            if (caller.className.equals(member.owner.className))
                return ErrorOr.Success();

            return ErrorOr.Fail("Membro '" + keyName + "' privado não pode ser acessado nesse contexto.");
        }

        return isSameOrSubclass(caller, member.owner);
    }
}
