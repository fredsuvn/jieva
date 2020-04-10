package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.map.MapHelper;

import java.util.Collections;
import java.util.Map;

public class BeanStructSupport {

    public static BeanStructBuilder newBuilder() {
        return new BeanStructBuilder();
    }

    public static class BeanStructBuilder extends CacheStateBuilder<BeanStruct> {

        private @Nullable Class<?> type;
        private @Nullable Map<String, BeanProperty> propertyMap;
        private @Nullable Map<String, BeanMethod> methodMap;

        public BeanStructBuilder setType(Class<?> type) {
            this.changeState();
            this.type = type;
            return this;
        }

        public BeanStructBuilder setProperties(Map<String, BeanProperty> properties) {
            this.changeState();
            this.propertyMap = properties;
            return this;
        }

        public BeanStructBuilder setMethods(Map<String, BeanMethod> methodMap) {
            this.changeState();
            this.methodMap = methodMap;
            return this;
        }

        @Override
        protected BeanStruct buildNew() {
            return new BeanStructImpl(this);
        }

        private static final class BeanStructImpl implements BeanStruct {

            private final Class<?> type;
            private final Map<String, BeanProperty> propertyMap;
            private final Map<String, BeanProperty> readablePropertyMap;
            private final Map<String, BeanProperty> writeablePropertyMap;
            private final Map<String, BeanMethod> methodMap;

            private BeanStructImpl(BeanStructBuilder builder) {
                if (builder.type == null) {
                    throw new IllegalStateException("Type cannot be null.");
                }
                this.type = builder.type;
                this.propertyMap = builder.propertyMap == null ?
                        Collections.emptyMap() : MapHelper.immutable(builder.propertyMap);
                this.readablePropertyMap = MapHelper.filter(this.propertyMap, e -> e.getValue().isReadable());
                this.writeablePropertyMap = MapHelper.filter(this.propertyMap, e -> e.getValue().isWriteable());
                this.methodMap = builder.methodMap == null ?
                        Collections.emptyMap() : MapHelper.immutable(builder.methodMap);
            }

            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public @Nullable BeanProperty getProperty(String propertyName) {
                return propertyMap.get(propertyName);
            }

            @Override
            public @Immutable Map<String, BeanProperty> getAllProperties() {
                return propertyMap;
            }

            @Override
            public @Immutable Map<String, BeanProperty> getReadableProperties() {
                return readablePropertyMap;
            }

            @Override
            public @Immutable Map<String, BeanProperty> getWriteableProperties() {
                return writeablePropertyMap;
            }

            @Override
            public @Nullable BeanMethod getMethodBySignature(String methodSignature) {
                return methodMap.get(methodSignature);
            }

            @Override
            public @Immutable Map<String, BeanMethod> getAllMethods() {
                return methodMap;
            }
        }
    }
}
