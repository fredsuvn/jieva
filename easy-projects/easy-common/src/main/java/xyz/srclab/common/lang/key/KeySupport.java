package xyz.srclab.common.lang.key;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.list.ListHelper;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

final class KeySupport {

    static Key buildCacheKey(Object... keyComponents) {
        return new KeyImpl(keyComponents);
    }

    private static final class KeyImpl implements Key {

        private final List<Object> keyComponents;
        private final int hashCode;
        private final String toString;

        private KeyImpl(Object... keyComponents) {
            this.keyComponents = buildKeyComponents(keyComponents);
            this.hashCode = this.keyComponents.hashCode();
            this.toString = this.keyComponents.toString();
        }

        private List<Object> buildKeyComponents(Object... keyComponents) {
            if (!hasArray(keyComponents)) {
                return ListHelper.immutable(keyComponents);
            }
            List<Object> result = new LinkedList<>();
            for (Object keyComponent : keyComponents) {
                if (!keyComponent.getClass().isArray()) {
                    result.add(keyComponent);
                    continue;
                }
                resolveArrayAndPush(keyComponent, result);
            }
            return ListHelper.immutable(result);
        }

        private boolean hasArray(Object[] keyComponents) {
            for (Object keyComponent : keyComponents) {
                if (keyComponent.getClass().isArray()) {
                    return false;
                }
            }
            return true;
        }

        private void resolveArrayAndPush(Object array, List<Object> container) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(array, i);
                if (element.getClass().isArray()) {
                    resolveArrayAndPush(element, container);
                    continue;
                }
                container.add(element);
            }
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
            return hashCode;
        }

        @Override
        public List<Object> getKeyComponents() {
            return keyComponents;
        }

        @Override
        public String toString() {
            return toString;
        }
    }
}
