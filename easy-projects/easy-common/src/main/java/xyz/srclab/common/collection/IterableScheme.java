package xyz.srclab.common.collection;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface IterableScheme {

    static IterableScheme getIterableScheme(Type type) {
        return SchemeSupport.getIterableScheme(type);
    }

    Type iterableType();

    Class<? extends Iterable<?>> rawIterableType();

    Type elementType();
}
