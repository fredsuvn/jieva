package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author sunqian
 */
public class Hash {

    public static int hash(@Nullable Object any) {
        if (any == null) {
            return 0;
        }
        if (any instanceof Object[]) {
            return Arrays.deepHashCode((Object[]) any);
        }
        if (any instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) any);
        }
        if (any instanceof byte[]) {
            return Arrays.hashCode((byte[]) any);
        }
        if (any instanceof short[]) {
            return Arrays.hashCode((short[]) any);
        }
        if (any instanceof char[]) {
            return Arrays.hashCode((char[]) any);
        }
        if (any instanceof int[]) {
            return Arrays.hashCode((int[]) any);
        }
        if (any instanceof long[]) {
            return Arrays.hashCode((long[]) any);
        }
        if (any instanceof float[]) {
            return Arrays.hashCode((float[]) any);
        }
        if (any instanceof double[]) {
            return Arrays.hashCode((double[]) any);
        }
        return Objects.hashCode(any);
    }

    public static int hash(Object... objects) {
        return Arrays.deepHashCode(objects);
    }
}
