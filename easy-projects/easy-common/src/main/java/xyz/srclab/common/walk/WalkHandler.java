package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface WalkHandler {

    void doUnit(@Nullable Object unit, Type type);

    void doElement(int index, @Nullable Object value, Type type, Walker walker);

    void doEntry(Object index, Type indexType, @Nullable Object value, Type type, Walker walker);

    void beforeObject(@Nullable Object record, Type type);

    void afterObject(@Nullable Object record, Type type);

    void beforeList(@Nullable Object list, Type type);

    void afterList(@Nullable Object list, Type type);
}