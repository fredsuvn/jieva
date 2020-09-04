package xyz.srclab.common.base;

import xyz.srclab.annotation.NotNull;

/**
 * @author sunqian
 */
public class Cast {

    @NotNull
    public static <T, R> R as(@NotNull T any) throws ClassCastException {
        return (R) any;
    }

    public static <T, R> R asNullable(T any) throws ClassCastException {
        return any == null ? null : as(any);
    }
}
