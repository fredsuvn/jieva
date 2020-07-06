package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface Walker {

    static WalkerBuilder newBuilder() {
        return WalkerBuilder.newBuilder();
    }

    default void walk(Object any) {
        walk(any, any.getClass());
    }

    void walk(@Nullable Object any, Type type);
}
