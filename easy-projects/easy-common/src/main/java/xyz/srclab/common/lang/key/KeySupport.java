package xyz.srclab.common.lang.key;

import xyz.srclab.annotation.Nullable;

import java.util.Arrays;

final class KeySupport {

    static Key buildCacheKey(Object... keyComponents) {
        return new KeyImpl(keyComponents);
    }

    private static final class KeyImpl implements Key {

        private final Object[] keyComponents;
        private final int hashCode;
        private @Nullable String toString;

        private KeyImpl(Object... keyComponents) {
            this.keyComponents = keyComponents.clone();
            this.hashCode = Arrays.deepHashCode(keyComponents);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof KeyImpl)) {
                return false;
            }
            KeyImpl that = (KeyImpl) other;
            return Arrays.deepEquals(this.keyComponents, that.keyComponents);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public String toString() {
            if (toString == null) {
                this.toString = Arrays.deepToString(keyComponents);
            }
            return toString;
        }
    }
}
