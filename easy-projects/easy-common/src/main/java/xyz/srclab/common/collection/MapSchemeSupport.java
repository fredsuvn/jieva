package xyz.srclab.common.collection;

import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sunqian
 */
final class MapSchemeSupport {

    private static final Cache<Type, MapScheme> cache = Cache.newCommonCache();

    static MapScheme getMapScheme(Type type) {
        Type scheme = TypeKit.getGenericInterface(type, Map.class);
        if (scheme instanceof Class) {
            return new MapSchemeImpl(Object.class, Object.class);
        }
        if (scheme instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) scheme).getActualTypeArguments();
            return
        }
    }

    private static MapScheme getMapScheme0(Type type) {
        Type scheme = TypeKit.getGenericInterface(type, Map.class);
        if (scheme instanceof Class) {
            return new MapSchemeImpl(Object.class, Object.class);
        }
        if (scheme instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) scheme).getActualTypeArguments();
            return new MapSchemeImpl(types[0], types[1]);
        }
        //if (scheme instanceof )
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
    }
}
