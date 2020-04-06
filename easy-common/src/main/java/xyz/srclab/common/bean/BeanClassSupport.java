package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.map.MapHelper;

import java.util.Collections;
import java.util.Map;

public class BeanClassSupport {

    public static BeanClassBuilder newBuilder() {
        return new BeanClassBuilder();
    }

    public static class BeanClassBuilder extends CacheStateBuilder<BeanClass> {

        private @Nullable Class<?> type;
        private @Nullable Map<String, BeanProperty> propertyMap;
        private @Nullable Map<String, BeanMethod> methodMap;

        public BeanClassBuilder setType(Class<?> type) {
            this.changeState();
            this.type = type;
            return this;
        }

        public BeanClassBuilder setProperties(Map<String, BeanProperty> properties) {
            this.changeState();
            this.propertyMap = properties;
            return this;
        }

        public BeanClassBuilder setMethods(Map<String, BeanMethod> methodMap) {
            this.changeState();
            this.methodMap = methodMap;
            return this;
        }

        @Override
        protected BeanClass buildNew() {
            return new BeanClassImpl(this);
        }

        private static final class BeanClassImpl implements BeanClass {

            private final Class<?> type;
            private final Map<String, BeanProperty> propertyMap;
            private final Map<String, BeanMethod> methodMap;

            private BeanClassImpl(BeanClassBuilder builder) {
                if (builder.type == null) {
                    throw new IllegalStateException("Type cannot be null.");
                }
                this.type = builder.type;
                this.propertyMap = builder.propertyMap == null ?
                        Collections.emptyMap() : MapHelper.immutable(builder.propertyMap);
                this.methodMap = builder.methodMap == null ?
                        Collections.emptyMap() : MapHelper.immutable(builder.methodMap);
            }

            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public boolean containsProperty(String propertyName) {
                return propertyMap.containsKey(propertyName);
            }

            @Override
            public BeanProperty getProperty(String propertyName) {
                if (!propertyMap.containsKey(propertyName)) {
                    throw new BeanPropertyNotFoundException(propertyName);
                }
                return propertyMap.get(propertyName);
            }

            @Override
            public @Immutable Map<String, BeanProperty> getAllProperties() {
                return propertyMap;
            }

            @Override
            public boolean containsMethod(String methodSignature) {
                return methodMap.containsKey(methodSignature);
            }

            @Override
            public BeanMethod getMethodBySignature(String methodSignature) throws BeanMethodNotFoundException {
                if (!methodMap.containsKey(methodSignature)) {
                    throw new BeanMethodNotFoundException(methodSignature);
                }
                return methodMap.get(methodSignature);
            }

            @Override
            public @Immutable Map<String, BeanMethod> getAllMethods() {
                return methodMap;
            }
        }
    }
}
