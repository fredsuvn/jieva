package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.reflect.SignatureHelper;

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

    default @Nullable BeanMethod getMethod(String methodName, Class<?>... parameterTypes) {
        return getMethodBySignature(SignatureHelper.signMethod(methodName, parameterTypes));
    }

    @Nullable
    BeanMethod getMethodBySignature(String methodSignature);

    /**
     * Keys are method signatures.
     *
     * @return Immutable map contains method signature and bean method
     */
    @Immutable
    Map<String, BeanMethod> getAllMethods();

    class Builder extends CacheStateBuilder<BeanStruct> {

        private final Class<?> type;
        private @Nullable Map<String, BeanProperty> propertyMap;
        private @Nullable Map<String, BeanMethod> methodMap;

        public Builder(Class<?> type) {
            this.type = type;
        }

        public Builder setProperties(Map<String, BeanProperty> properties) {
            this.changeState();
            this.propertyMap = properties;
            return this;
        }

        public Builder setMethods(Map<String, BeanMethod> methodMap) {
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
