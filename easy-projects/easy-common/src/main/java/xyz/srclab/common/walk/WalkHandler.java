package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface WalkHandler {

    void doUnit(@Nullable Object unit, Type type);

    void doEntry(Object key, Type keyType, @Nullable Object value, Type type, Walker walker);

    void beforeList(@Nullable Object list, Type type, Walker walker);

    void afterList(@Nullable Object list, Type type, Walker walker);

    void beforeRecord(@Nullable Object record, Type type, Walker walker);

    void afterRecord(@Nullable Object record, Type type, Walker walker);
}