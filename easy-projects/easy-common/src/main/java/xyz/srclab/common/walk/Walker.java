package xyz.srclab.common.walk;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface Walker {

    default void walk(Object any) {
        walk(any, any.getClass());
    }

    void walk(Object any, Type type);
}
