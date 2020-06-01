package xyz.srclab.common.lang.key;

import xyz.srclab.common.array.ArrayHelper;

import java.util.Arrays;

/**
 * @author sunqian
 */
final class Key0 {

    static Key newKey(Object... elements) {
        return new KeyImpl(elements);
    }

    static Key newKey(Iterable<?> elements) {
        return new KeyImpl(elements);
    }

    private static final class KeyImpl implements Key {

        private final Object[] elements;

        private KeyImpl(Object... elements) {
            this.elements = elements.clone();
        }

        private KeyImpl(Iterable<?> elements) {
            this((Object[]) ArrayHelper.toArray(elements, Object.class));
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            KeyImpl key = (KeyImpl) object;
            return Arrays.equals(elements, key.elements);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(elements);
        }

        @Override
        public String toString() {
            return Arrays.toString(elements);
        }
    }
}
