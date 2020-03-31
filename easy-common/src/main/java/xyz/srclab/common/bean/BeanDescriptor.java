package xyz.srclab.common.bean;

import com.sun.javafx.fxml.PropertyNotFoundException;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.map.MapHelper;

import java.util.Collections;
import java.util.Map;

@Immutable
public interface BeanDescriptor {

    static Builder newBuilder() {
        return new Builder();
    }

    Class<?> getType();

    boolean containsProperty(String propertyName);

    default boolean canReadProperty(String propertyName) {
        if (!containsProperty(propertyName)) {
            return false;
        }
        return getPropertyDescriptor(propertyName).isReadable();
    }

    default boolean canWriteProperty(String propertyName) {
        if (!containsProperty(propertyName)) {
            return false;
        }
        return getPropertyDescriptor(propertyName).isWriteable();
    }

    BeanPropertyDescriptor getPropertyDescriptor(String propertyName) throws PropertyNotFoundException;

    @Immutable
    Map<String, BeanPropertyDescriptor> getPropertyDescriptors();

    class Builder extends CacheStateBuilder<BeanDescriptor> {

        private @Nullable Class<?> type;
        private @Nullable Map<String, BeanPropertyDescriptor> properties;

        public Builder setType(Class<?> type) {
            this.changeState();
            this.type = type;
            return this;
        }

        public Builder setProperties(Map<String, BeanPropertyDescriptor> properties) {
            this.changeState();
            this.properties = properties;
            return this;
        }

        @Override
        protected BeanDescriptor buildNew() {
            return new BeanDescriptorImpl(this);
        }

        private static final class BeanDescriptorImpl implements BeanDescriptor {

            private final Class<?> type;
            private final Map<String, BeanPropertyDescriptor> properties;

            private BeanDescriptorImpl(Builder builder) {
                if (builder.type == null) {
                    throw new IllegalStateException("Type cannot be null.");
                }
                this.type = builder.type;
                this.properties = builder.properties == null ?
                        Collections.emptyMap() : MapHelper.immutableMap(builder.properties);
            }

            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public boolean containsProperty(String propertyName) {
                return properties.containsKey(propertyName);
            }

            @Override
            public BeanPropertyDescriptor getPropertyDescriptor(String propertyName) {
                if (!properties.containsKey(propertyName)) {
                    throw new BeanPropertyNotFoundException(propertyName);
                }
                return properties.get(propertyName);
            }

            @Override
            public Map<String, BeanPropertyDescriptor> getPropertyDescriptors() {
                return properties;
            }
        }
    }
}
