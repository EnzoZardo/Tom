package Entities.Constants;

import Entities.Enums.Runtime.ValueType;

import java.util.HashMap;

public record ReservedPrimitiveTypes()
{
    private final static HashMap<String, ValueType> relations = new HashMap<>()
    {{
        //TODO: add new types
        put(ReservedKeys.Char, null);
        put(ReservedKeys.Null, ValueType.Null);
        put(ReservedKeys.Float, ValueType.Numeric);
        put(ReservedKeys.String, ValueType.String);
        put(ReservedKeys.Object, ValueType.Object);
        put(ReservedKeys.Integer, ValueType.Numeric);
        put(ReservedKeys.Boolean, ValueType.Boolean);
    }};

    public static boolean isReserved(String value)
    {
        return relations.containsKey(value);
    }

    public static ValueType getValue(String value)
    {
        return relations.get(value);
    }
}
