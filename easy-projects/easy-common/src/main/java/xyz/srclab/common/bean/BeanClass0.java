package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sunqian
 */
final class BeanClass0 {

    static BeanClass newBeanClass(Class<?> type, Map<String, BeanProperty> propertyMap) {
        return new BeanClassImpl(type, propertyMap);
    }

    static Map<String, @Nullable Object> newBeanViewMap(Object bean, Map<String, BeanProperty> properties) {
        return new BeanPropertiesView(bean, properties);
    }

    private static final class BeanClassImpl implements BeanClass {

        protected final Class<?> type;
        protected final @Immutable Map<String, BeanProperty> properties;

        protected final @Immutable Map<String, BeanProperty> readableProperties;
        protected final @Immutable Map<String, BeanProperty> writeableProperties;

        public BeanClassImpl(Class<?> type, Map<String, BeanProperty> properties) {
            this.type = type;
            this.properties = MapKit.immutable(properties);
            this.readableProperties = MapKit.filter(this.properties, (name, property) -> property.readable());
            this.writeableProperties = MapKit.filter(this.properties, (name, property) -> property.writeable());
        }

        @Override
        public Class<?> type() {
            return type;
        }

        @Override
        public @Immutable Map<String, BeanProperty> properties() {
            return properties;
        }

        @Override
        public @Immutable Map<String, BeanProperty> readableProperties() {
            return readableProperties;
        }

        @Override
        public @Immutable Map<String, BeanProperty> writeableProperties() {
            return writeableProperties;
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof BeanClassImpl) {
                return type.equals(((BeanClassImpl) o).type);
            }
            return false;
        }

        @Override
        public String toString() {
            return "bean " + type.getName();
        }
    }

    private static final class BeanPropertiesView
            extends AbstractMap<String, @Nullable Object> implements Map<String, @Nullable Object> {

        private final Object bean;
        private final @Immutable Map<String, BeanProperty> properties;

        private @Nullable Set<Entry<String, @Nullable Object>> entrySet;

        private BeanPropertiesView(Object bean, Map<String, BeanProperty> properties) {
            this.bean = bean;
            this.properties = MapKit.immutable(properties);
        }

        @Override
        public Set<Entry<String, @Nullable Object>> entrySet() {
            if (entrySet == null) {
                entrySet = newEntrySet();
            }
            return entrySet;
        }

        @Override
        public Object put(String key, Object value) {
            @Nullable BeanProperty beanProperty = properties.get(key);
            if (beanProperty == null) {
                throw new UnsupportedOperationException("Property not found: " + key);
            }
            return put0(beanProperty, value);
        }

        private Set<Entry<String, @Nullable Object>> newEntrySet() {
            return properties.entrySet()
                    .stream()
                    .map(e -> new Entry<String, @Nullable Object>() {
                                @Override
                                public String getKey() {
                                    return e.getKey();
                                }

                                @Nullable
                                @Override
                                public Object getValue() {
                                    return e.getValue().getValue(bean);
                                }

                                @Nullable
                                @Override
                                public Object setValue(@Nullable Object value) {
                                    BeanProperty beanProperty = e.getValue();
                                    return put0(beanProperty, value);
                                }
                            }
                    )
                    .collect(Collectors.toSet());
        }

        @Nullable
        private Object put0(BeanProperty property, @Nullable Object value) {
            if (!property.writeable()) {
                throw new UnsupportedOperationException("Property is not writeable: " + property.name());
            }
            @Nullable Object old = property.readable() ? property.getValue(bean) : null;
            property.setValue(bean, value);
            return old;
        }
    }
}
