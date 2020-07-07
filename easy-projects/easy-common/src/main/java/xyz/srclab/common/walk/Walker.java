package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface Walker<C> {

    static <C> WalkerBuilder<C> newBuilder(WalkHandler<C> walkHandler) {
        return WalkerBuilder.newBuilder(walkHandler);
    }

    default C walk(Object any) {
        return walk(any, any.getClass());
    }

    C walk(@Nullable Object any, Type type);
}
