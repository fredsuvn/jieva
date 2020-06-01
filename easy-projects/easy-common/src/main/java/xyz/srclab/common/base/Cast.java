package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public class Cast {

    public static <T, R> R as(T any) throws ClassCastException {
        return (R) any;
    }

    @Nullable
    public static <T, R> R nullable(@Nullable T any) throws ClassCastException {
        return any == null ? null : as(any);
    }
}
