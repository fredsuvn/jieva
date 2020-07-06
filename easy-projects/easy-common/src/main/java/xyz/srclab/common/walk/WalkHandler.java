package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.stack.Stack;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface WalkHandler<C> {

    C newContext();

    C newContext(C lastContext);

    void doUnit(@Nullable Object unit, Type type, Stack<C> contextStack);

    void beforeList(@Nullable Object list, Type type, Stack<C> contextStack);

    void doListElement(
            int index, @Nullable Object value, Type type, Stack<C> contextStack, Walker<C> walker);

    void afterList(@Nullable Object list, Type type, Stack<C> contextStack);

    void beforeObject(@Nullable Object record, Type type, Stack<C> contextStack);

    void doObjectElement(
            Object index, Type indexType, @Nullable Object value, Type type, Stack<C> contextStack, Walker<C> walker);

    void afterObject(@Nullable Object object, Type type, Stack<C> contextStack);
}