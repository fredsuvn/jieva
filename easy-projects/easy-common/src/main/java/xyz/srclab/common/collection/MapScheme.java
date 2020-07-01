package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
@Immutable
public interface MapScheme {

    static MapScheme getMapScheme(Type keyType, Type valueType) {
        return new MapScheme() {
            @Override
            public Type keyType() {
                return keyType;
            }

            @Override
            public Type valueType() {
                return valueType;
            }
        };
    }

    Type keyType();

    Type valueType();
}
