package xyz.srclab.common.lang;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayHelper;

import java.util.Arrays;

/**
 * @author sunqian
 */
@Immutable
public abstract class Key {

    public static Key from(Object... keyComponents) {
        return new KeyImpl(keyComponents);
    }

    public static Key from(Iterable<?> keyComponents) {
        return new KeyImpl(keyComponents);
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object other);

    private static final class KeyImpl extends Key {

        private static final long HASH_CODE_MASK = 0xffffffff00000000L;

        private final Object[] keyComponents;

        private long hashCode;
        private @Nullable String toString;

        private KeyImpl(Object... keyComponents) {
            this.keyComponents = keyComponents.clone();
        }

        private KeyImpl(Iterable<?> keyComponents) {
            this.keyComponents = ArrayHelper.toArray(keyComponents, Object.class);
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
            if ((hashCode & HASH_CODE_MASK) == 0L) {
                hashCode = Arrays.deepHashCode(keyComponents) | HASH_CODE_MASK;
            }
            return (int) hashCode;
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
