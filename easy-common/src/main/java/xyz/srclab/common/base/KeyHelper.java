package xyz.srclab.common.base;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.common.reflect.SignatureHelper;

public class KeyHelper {

    public static Object buildKey(Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(":");
            stringBuilder.append(buildKey(arg));
        }
        return stringBuilder.toString();
    }

    public static Object buildKey(Object any) {
        if (any instanceof Class) {
            return SignatureHelper.signature((Class<?>) any);
        }
        return String.valueOf(any);
    }
}
