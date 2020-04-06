package xyz.srclab.common.base;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import xyz.srclab.common.array.ArrayHelper;

public class KeyHelper {

    public static Object buildKey(Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return "";
        }
        Object[] keys = ArrayHelper.newArray(new String[args.length], 0, args.length,
                i -> buildKey(args[i]));
        return StringUtils.join(keys, ":");
    }

    public static Object buildKey(Object any) {
        if (any instanceof Class) {
            return ((Class<?>) any).getName();
        }
        return String.valueOf(any);
    }
}
