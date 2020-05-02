package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.method.MethodHelper;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

@Immutable
public interface BeanStruct {

    static Builder newBuilder(Class<?> type) {
        return new Builder(type);
    }

    Class<?> getType();

    default boolean canReadProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.isReadable();
    }

    default boolean canWriteProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.isWriteable();
    }

    @Nullable
    BeanProperty getProperty(String propertyName);

    @Immutable
    Map<String, BeanProperty> getAllProperties();

    @Immutable
    Map<String, BeanProperty> getReadableProperties();

    @Immutable
    Map<String, BeanProperty> getWriteableProperties();

    @Nullable
    BeanMethod getMethod(String methodName, Class<?>... parameterTypes);

    @Nullable
    BeanMethod getMethod(Method method);

    @Immutable
    Map<Method, BeanMethod> getAllMethods();

    final class Builder extends CachedBuilder<BeanStruct> {

        private final Class<?> type;
        private @Nullable Map<String, BeanProperty> propertyMap;
        private @Nullable Map<Method, BeanMethod> methodMap;

        public Builder(Class<?> type) {
            this.type = type;
        }

        public Builder setProperties(Map<String, BeanProperty> properties) {
            this.propertyMap = properties;
            this.updateState();
            return this;
        }

        public Builder setMethods(Map<Method, BeanMethod> methodMap) {
            this.methodMap = methodMap;
            this.updateState();
            return this;
        }

        @Override
        protected BeanStruct buildNew() {
            return new BeanStructImpl(this);
        }

        private static final class BeanStructImpl implements BeanStruct {

            private final Class<?> type;
            private final @Immutable Map<String, BeanProperty> propertyMap;
            private final @Immutable Map<String, BeanProperty> readablePropertyMap;
            private final @Immutable Map<String, BeanProperty> writeablePropertyMap;
            private final @Immutable Map<Method, BeanMethod> methodMap;

            private BeanStructImpl(Builder builder) {
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
            public @Nullable BeanMethod getMethod(String methodName, Class<?>... parameterTypes) {
                @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
                return method == null ? null : methodMap.get(method);
            }

            @Override
            public @Nullable BeanMethod getMethod(Method method) {
                return methodMap.get(method);
            }

            @Override
            public @Immutable Map<Method, BeanMethod> getAllMethods() {
                return methodMap;
            }
        }
    }
}
