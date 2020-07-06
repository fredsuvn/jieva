package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface WalkHandler<S> {

    S newStack();

    void doUnit(@Nullable Object unit, Type type, S stack);

    void beforeList(@Nullable Object list, Type type, S stack);

    void doListElement(int index, @Nullable Object value, Type type, S stack, Walker walker);

    void afterList(@Nullable Object list, Type type, S stack);

    void beforeObject(@Nullable Object record, Type type, S stack);

    void doObjectElement(Object index, Type indexType, @Nullable Object value, Type type, S stack, Walker walker);

    void afterObject(@Nullable Object record, Type type, S stack);
}