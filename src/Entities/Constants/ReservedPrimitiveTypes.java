package Entities.Constants;

import Entities.Enums.Runtime.ValueType;

import java.util.HashMap;

public record ReservedPrimitiveTypes()
{
    private final static HashMap<String, ValueType> relations = new HashMap<>()
    {{
        put(ReservedKeys.Integer, ValueType.Numeric);
        put(ReservedKeys.Boolean, ValueType.Boolean);
        put(ReservedKeys.Object, ValueType.Object);
        put(ReservedKeys.String, ValueType.String);
        put(ReservedKeys.Float, ValueType.Numeric);
        put(ReservedKeys.Null, ValueType.Null);
        put(ReservedKeys.Char, null);
    }};

    public static boolean isReserved(String value)
    {
        return relations.containsKey(value);
    }
}
