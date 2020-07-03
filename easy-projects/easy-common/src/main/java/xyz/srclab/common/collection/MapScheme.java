package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
@Immutable
public interface MapScheme {

    static MapScheme getMapScheme(Type type) {
        return SchemeSupport.getMapScheme(type);
    }

    static MapScheme newMapScheme(Type keyType, Type valueType) {
        return SchemeSupport.newMapScheme(keyType, valueType);
    }

    Type keyType();

    Type valueType();
}
