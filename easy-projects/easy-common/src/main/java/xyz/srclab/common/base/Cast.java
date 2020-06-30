package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public class Cast {

    public static <T, R> R as(T any) throws ClassCastException {
        return (R) any;
    }

    @Nullable
    public static <T, R> R asNullable(@Nullable T any) throws ClassCastException {
        return any == null ? null : as(any);
    }

    public static boolean canCast(Object instanceOrType, Class<?> target) {
        if (instanceOrType instanceof Class) {
            return target.isAssignableFrom((Class<?>) instanceOrType);
        }
        if (instanceOrType instanceof Type) {
            return false;
        }
        return target.isAssignableFrom(instanceOrType.getClass());
    }
}
