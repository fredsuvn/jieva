package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.FieldKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sunqian
 */
public final class BeanSupport {

    public static BeanClass newBeanClass(Class<?> type, Map<String, BeanProperty> propertyMap) {
        return new BeanClassImpl(type, propertyMap);
    }

    public static BeanProperty newBeanPropertyOnField(Class<?> ownerType, Field field) {
        return new BeanPropertyOnField(ownerType, field);
    }

    public static BeanProperty newBeanPropertyOnMethods(
            Class<?> ownerType, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
        return new BeanPropertyOnMethods(ownerType, name, readMethod, writeMethod);
    }

    public static Map<String, Object> newBeanViewMap(BeanClass beanClass, Object bean) {
        return new BeanViewMap(beanClass, bean);
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

    private static final class BeanPropertyOnField implements BeanProperty {

        private final Class<?> ownerType;

        private final Field field;
        private final List<Annotation> fieldAnnotations;

        private BeanPropertyOnField(Class<?> ownerType, Field field) {
            this.ownerType = ownerType;
            this.field = field;
            this.fieldAnnotations = ListKit.immutable(this.field.getAnnotations());
        }

        @Override
        public String name() {
            return field.getName();
        }

        @Override
        public Class<?> type() {
            return field.getType();
        }

        @Override
        public Type genericType() {
            return field.getGenericType();
        }

        @Override
        public Class<?> ownerType() {
            return ownerType;
        }

        @Override
        public boolean readable() {
            return true;
        }

        @Override
        public boolean writeable() {
            return true;
        }

        @Override
        public @Nullable Object getValue(Object bean) throws UnsupportedOperationException {
            return FieldKit.getValue(field, bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            FieldKit.setValue(field, bean, value);
        }

        @Override
        public Field tryField() {
            return field;
        }

        @Override
        public @Immutable List<Annotation> tryFieldAnnotations() {
            return fieldAnnotations;
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof BeanPropertyOnField) {
                BeanPropertyOnField that = (BeanPropertyOnField) obj;
                return this.type().equals(that.type()) && this.field.equals(that.field);
            }
            return false;
        }

        @Override
        public String toString() {
            return name() + ": " + genericType().toString();
        }
    }

    private static final class BeanPropertyOnMethods implements BeanProperty {

        private final String name;
        private final Class<?> type;
        private final Type genericType;
        private final Class<?> ownerType;

        private final @Nullable Method readMethod;
        private @Nullable MethodInvoker getter;
        private final @Nullable Method writeMethod;
        private @Nullable MethodInvoker setter;

        private final @Nullable Field field;
        private final List<Annotation> fieldAnnotations;

        private BeanPropertyOnMethods(
                Class<?> ownerType, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
            this.ownerType = ownerType;
            this.name = name;
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;

            if (readMethod == null) {
                Checker.checkState(writeMethod != null, "both read and write method are null");
                this.type = writeMethod.getParameterTypes()[0];
                this.genericType = writeMethod.getGenericParameterTypes()[0];
            } else {
                this.type = readMethod.getReturnType();
                this.genericType = readMethod.getGenericReturnType();
            }

            this.field = FieldKit.getDeclaredField(this.type, name);
            this.fieldAnnotations = this.field == null ? Collections.emptyList() :
                    ListKit.immutable(this.field.getAnnotations());
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Class<?> type() {
            return type;
        }

        @Override
        public Type genericType() {
            return genericType;
        }

        @Override
        public Class<?> ownerType() {
            return ownerType;
        }

        @Override
        public boolean readable() {
            return readMethod != null;
        }

        @Override
        public boolean writeable() {
            return writeMethod != null;
        }

        @Override
        public @Nullable Object getValue(Object bean) throws UnsupportedOperationException {
            if (!readable()) {
                throw new UnsupportedOperationException("property is not readable: " + name());
            }
            if (getter == null) {
                synchronized (readMethod) {
                    if (getter == null) {
                        getter = MethodInvoker.of(readMethod);
                    }
                }
            }
            return getter.invoke(bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            if (!writeable()) {
                throw new UnsupportedOperationException("property is not writeable: " + name());
            }
            if (setter == null) {
                synchronized (writeMethod) {
                    if (setter == null) {
                        setter = MethodInvoker.of(writeMethod);
                    }
                }
            }
            setter.invoke(bean, value);
        }

        @Override
        public @Nullable Field tryField() {
            return field;
        }

        @Override
        public @Immutable List<Annotation> tryFieldAnnotations() {
            return fieldAnnotations;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof BeanPropertyOnMethods) {
                BeanPropertyOnMethods that = (BeanPropertyOnMethods) obj;
                return this.type.equals(that.type) && this.name.equals(that.name);
            }
            return false;
        }

        @Override
        public String toString() {
            return name + ": " + genericType.toString();
        }
    }

    private static final class BeanViewMap extends AbstractMap<String, Object> implements Map<String, Object> {

        private final BeanClass beanClass;
        private final Object bean;

        private @Nullable Set<Entry<String, Object>> entrySet;

        private BeanViewMap(BeanClass beanClass, Object bean) {
            this.beanClass = beanClass;
            this.bean = bean;
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            if (entrySet == null) {
                entrySet = newEntrySet();
            }
            return entrySet;
        }

        @Override
        public Object put(String key, Object value) {
            @Nullable BeanProperty beanProperty = beanClass.property(key);
            if (beanProperty == null || !beanProperty.writeable()) {
                throw new UnsupportedOperationException("Property not found: " + key);
            }
            @Nullable Object old = beanProperty.readable() ? beanProperty.getValue(bean) : null;
            beanProperty.setValue(bean, value);
            return old;
        }

        private Set<Entry<String, Object>> newEntrySet() {
            return beanClass.properties()
                    .entrySet()
                    .stream()
                    .map(e -> new Entry<String, Object>() {
                                @Override
                                public String getKey() {
                                    return e.getKey();
                                }

                                @Override
                                @Nullable
                                public Object getValue() {
                                    return e.getValue().getValue(bean);
                                }

                                @Override
                                @Nullable
                                public Object setValue(Object value) {
                                    BeanProperty beanProperty = e.getValue();
                                    @Nullable Object old = beanProperty.readable() ? beanProperty.getValue(bean) : null;
                                    beanProperty.setValue(bean, value);
                                    return old;
                                }
                            }
                    )
                    .collect(Collectors.toSet());
        }
    }
}
