package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface WalkHandler {

    void doUnit(@Nullable Object unit, Type type);

    void doEntry(Object key, Type keyType, @Nullable Object value, Type type);

    void beforeList(@Nullable Object list, Type type);

    void afterList(@Nullable Object list, Type type);

    void beforeRecord(@Nullable Object record, Type type);

    void afterRecord(@Nullable Object record, Type type);
}
