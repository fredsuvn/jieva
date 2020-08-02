package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class ToString {

    public static String toString(@Nullable Object any) {
        if (any == null) {
            return String.valueOf((Object) null);
        }
        return deepToString(any);
    }

    private static String deepToString(Object any) {
        if (any instanceof Object[]) {
            return Arrays.deepToString((Object[]) any);
        }
        if (any instanceof boolean[]) {
            return Arrays.toString((boolean[]) any);
        }
        if (any instanceof byte[]) {
            return Arrays.toString((byte[]) any);
        }
        if (any instanceof short[]) {
            return Arrays.toString((short[]) any);
        }
        if (any instanceof char[]) {
            return Arrays.toString((char[]) any);
        }
        if (any instanceof int[]) {
            return Arrays.toString((int[]) any);
        }
        if (any instanceof long[]) {
            return Arrays.toString((long[]) any);
        }
        if (any instanceof float[]) {
            return Arrays.toString((float[]) any);
        }
        if (any instanceof double[]) {
            return Arrays.toString((double[]) any);
        }
        return any.toString();
    }
}
