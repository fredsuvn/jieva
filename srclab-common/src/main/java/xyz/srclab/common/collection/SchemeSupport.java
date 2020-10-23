package xyz.srclab.common.collection;

import xyz.srclab.common.base.As;
import xyz.srclab.common.base.Hash;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sunqian
 */
final class SchemeSupport {

    static IterableScheme getIterableScheme(Type iterableType) {
        return IterableSchemeImpl.getImpl(iterableType);
    }

    static MapScheme getMapScheme(Type mapType) {
        return MapSchemeImpl.getImpl(mapType);
    }

    private static final class IterableSchemeImpl implements IterableScheme {

        public static IterableSchemeImpl getImpl(Type iterableType) {
            return cache.getNonNull(iterableType, IterableSchemeImpl::new);
        }

        private static final Cache<Type, IterableSchemeImpl> cache = Cache.commonCache();

        private final Type iterableType;
        private final Class<? extends Iterable<?>> rawIterableType;
        private final Type elementType;

        private IterableSchemeImpl(Type iterableType) {
            this.iterableType = iterableType;
            Type scheme = TypeKit.genericInterfaceFor(iterableType, Iterable.class);
            if (scheme instanceof Class) {
                this.rawIterableType = As.notNull(TypeKit.getUpperBoundClass(scheme));
                this.elementType = Object.class;
            } else if (scheme instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) scheme).getActualTypeArguments();
                this.rawIterableType = As.notNull(TypeKit.getUpperBoundClass(scheme));
                this.elementType = types[0];
            }
            throw new IllegalArgumentException("Unexpected type: " + scheme);
        }

        @Override
        public Type iterableType() {
            return iterableType;
        }

        @Override
        public Class<? extends Iterable<?>> rawIterableType() {
            return rawIterableType;
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
            return iterableType.equals(iterableScheme.iterableType);
        }

        @Override
        public int hashCode() {
            return Hash.hash(iterableType);
        }

        @Override
        public String toString() {
            return iterableType.getTypeName();
        }
    }

    private static final class MapSchemeImpl implements MapScheme {

        public static MapSchemeImpl getImpl(Type mapType) {
            return cache.getNonNull(mapType, MapSchemeImpl::new);
        }

        private static final Cache<Type, MapSchemeImpl> cache = Cache.commonCache();

        private final Type mapType;
        private final Class<? extends Map<?, ?>> rawMapType;
        private final Type keyType;
        private final Type valueType;

        private MapSchemeImpl(Type mapType) {
            this.mapType = mapType;
            Type scheme = TypeKit.genericInterfaceFor(mapType, Map.class);
            if (scheme instanceof Class) {
                this.rawMapType = As.notNull(TypeKit.getUpperBoundClass(scheme));
                this.keyType = Object.class;
                this.valueType = Object.class;
            } else if (scheme instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) scheme).getActualTypeArguments();
                this.rawMapType = As.notNull(TypeKit.getUpperBoundClass(scheme));
                this.keyType = types[0];
                this.valueType = types[1];
            }
            throw new IllegalArgumentException("Unexpected type: " + scheme);
        }

        @Override
        public Type mapType() {
            return mapType;
        }

        @Override
        public Class<? extends Map<?, ?>> rawMapType() {
            return rawMapType;
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
            return mapType.equals(mapScheme.mapType);
        }

        @Override
        public int hashCode() {
            return Hash.hash(mapType);
        }

        @Override
        public String toString() {
            return mapType.getTypeName();
        }
    }
}
