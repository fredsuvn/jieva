package xyz.srclab.common.collection;

import xyz.srclab.common.base.Hash;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * @author sunqian
 */
final class SchemeSupport {

    private static final Cache<Type, MapScheme> cache = Cache.newCommonCache();

    static MapScheme newMapScheme(Type keyType, Type valueType) {
        return new MapSchemeImpl(keyType, valueType);
    }

    static MapScheme getMapScheme(Type type) {
        return cache.getNonNull(type, SchemeSupport::getMapScheme0);
    }

    private static MapScheme getMapScheme0(Type type) {
        Type scheme = TypeKit.getGenericInterface(type, Map.class);
        if (scheme instanceof Class) {
            return MapScheme.newMapScheme(Object.class, Object.class);
        }
        if (scheme instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) scheme).getActualTypeArguments();
            return MapScheme.newMapScheme(types[0], types[1]);
        }
        throw new IllegalStateException("Unexpected type: " + scheme);
    }

    private static final class IterableSchemeImpl implements IterableScheme {

        private final Type elementType;

        private IterableSchemeImpl(Type elementType) {
            this.elementType = elementType;
        }

        @Override
        public Type elementType() {
            return elementType;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || !getClass().equals(object.getClass())) {
                return false;
            }
            IterableSchemeImpl iterableScheme = (IterableSchemeImpl) object;
            return elementType.equals(iterableScheme.elementType)
        }

        @Override
        public int hashCode() {
            return Hash.hash(elementType);
        }

        @Override
        public String toString() {
            return "Map<" + keyType.getTypeName() + ", " + valueType.getTypeName() + ">";
        }
    }

    private static final class MapSchemeImpl implements MapScheme {

        private final Type keyType;
        private final Type valueType;

        private MapSchemeImpl(Type keyType, Type valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        @Override
        public Type keyType() {
            return keyType;
        }

        @Override
        public Type valueType() {
            return valueType;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || !getClass().equals(object.getClass())) {
                return false;
            }
            MapSchemeImpl mapScheme = (MapSchemeImpl) object;
            return keyType.equals(mapScheme.keyType) &&
                    valueType.equals(mapScheme.valueType);
        }

        @Override
        public int hashCode() {
            return Hash.hash(keyType, valueType);
        }

        @Override
        public String toString() {
            return "Map<" + keyType.getTypeName() + ", " + valueType.getTypeName() + ">";
        }
    }
}
