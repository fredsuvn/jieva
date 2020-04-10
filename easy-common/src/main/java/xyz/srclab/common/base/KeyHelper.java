package xyz.srclab.common.base;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.reflect.SignatureHelper;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class KeyHelper {

    public static Object buildKey(Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return "";
        }
        Object[] keys = ArrayHelper.newArray(new String[args.length],
                i -> buildKey(args[i]));
        return StringUtils.join(keys, ":");
    }

    public static Object buildKey(Object any) {
        if (any instanceof Class) {
            return SignatureHelper.signClass((Class<?>) any);
        }
        if (any instanceof TypeVariable) {
            return ((TypeVariable<?>) any).getGenericDeclaration().toString() + any.toString();
        }
        if (any instanceof Type) {
            return ((Type) any).getTypeName();
        }
        return String.valueOf(any);
    }
}
