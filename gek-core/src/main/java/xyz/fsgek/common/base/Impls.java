package xyz.fsgek.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Type;

final class Impls {

    static GekOption<?, ?>[] EMPTY_OPTIONS = new GekOption<?, ?>[0];

    static GekObject nullGekObject() {
        return GekObjectImpl.NULL;
    }

    static GekObject newGekObject(@Nullable Object value, Type type) {
        return new GekObjectImpl(value, type);
    }

    static <K, V> GekOption<K, V> newGekOption(K key, V value) {
        return new GekOptionImpl<>(key, value);
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    private static final class GekObjectImpl implements GekObject {

        private static final GekObject NULL = new GekObjectImpl(null, Object.class);

        private final Object value;
        private final Type type;
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    private static final class GekOptionImpl<K, V> implements GekOption<K, V> {

        private final K key;
        private final V value;

        @Override
        public K getKey() {
            return null;
        }

        @Override
        public V getValue() {
            return null;
        }
    }
}
