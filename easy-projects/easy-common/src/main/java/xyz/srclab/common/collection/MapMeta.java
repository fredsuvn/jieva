package xyz.srclab.common.collection;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface MapMeta<K, V> {

    static <K, V> MapMeta<K, V> of(Type keyType, Type valueType) {
        return new MapMeta<K, V>() {
            @Override
            public Type getKeyType() {
                return keyType;
            }

            @Override
            public Type getValueType() {
                return valueType;
            }
        };
    }

    Type getKeyType();

    Type getValueType();
}
