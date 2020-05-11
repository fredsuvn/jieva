package xyz.srclab.common.bean;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.ToovaBoot;
import xyz.srclab.common.base.Checker;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sunqian
 */
final class BeanSupport {

    private static final BeanProvider beanProvider = ToovaBoot.getProvider(BeanProvider.class);

    static BeanOperator getBeanOperator() {
        return beanProvider.getBeanOperator();
    }

    static BeanConverter getBeanConverter() {
        return beanProvider.getBeanConverter();
    }

    static BeanConverterHandler getBeanConverterHandler() {
        return beanProvider.getBeanConverterHandler();
    }

    static BeanResolver getBeanResolver() {
        return beanProvider.getBeanResolver();
    }

    static BeanResolverHandler getBeanResolverHandler() {
        return beanProvider.getBeanResolverHandler();
    }

    static BeanProperty newMapProperty(Object key) {
        return new MapProperty(key);
    }

    private static final class MapProperty implements BeanProperty {

        private final Object key;
        private final String name;

        private MapProperty(Object key) {
            this.key = key;
            this.name = key.toString();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<?> getType() {
            return Object.class;
        }

        @Override
        public Type getGenericType() {
            return getType();
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public @Nullable Object getValue(Object bean) throws UnsupportedOperationException {
            Checker.checkArguments(bean instanceof Map, "Given object should be a Map");
            return ((Map) bean).get(key);
        }

        @Override
        public @Nullable Method getReadMethod() {
            return null;
        }

        @Override
        public boolean isWriteable() {
            return true;
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            Checker.checkArguments(bean instanceof Map, "Given object should be a Map");
            ((Map) bean).put(key, value);
        }

        @Override
        public @Nullable Method getWriteMethod() {
            return null;
        }
    }
}
