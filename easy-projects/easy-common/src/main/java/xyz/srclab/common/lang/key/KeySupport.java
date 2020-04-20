package xyz.srclab.common.lang.key;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.list.ListHelper;

import java.util.List;

public class KeySupport {

    static Key buildCacheKey(Object... keyComponents) {
        return new KeyImpl(keyComponents);
    }

    private static final class KeyImpl implements Key {

        private final List<Object> keyComponents;

        private KeyImpl(Object... keyComponents) {
            this.keyComponents = ListHelper.immutable(keyComponents);
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Key)) {
                return false;
            }
            Key that = (Key) other;
            return keyComponents.equals(that.getKeyComponents());
        }

        @Override
        public int hashCode() {
            return keyComponents.hashCode();
        }

        @Override
        public List<Object> getKeyComponents() {
            return keyComponents;
        }

        @Override
        public String toString() {
            return keyComponents.toString();
        }
    }
}
