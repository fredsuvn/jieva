package xyz.srclab.common.string;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public class ToStringKit {

    private static final ToString toString = ToString.defaultToString();

    public static String toString(@Nullable Object any) {
        return toString.toString(any);
    }
}
