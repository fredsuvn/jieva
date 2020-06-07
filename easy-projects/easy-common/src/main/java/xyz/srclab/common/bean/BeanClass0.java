package xyz.srclab.common.bean;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class BeanClass0 {

    static BeanClass newBeanClass(Class<?> type, Map<String, BeanProperty> propertyMap) {
        return new BeanClassImpl(type, propertyMap);
    }

    static BeanProperty newBeanPropertyOnField(Class<?> ownerType, Field field) {
        return new BeanPropertyOnField(ownerType, field);
    }

    static BeanProperty newBeanPropertyOnMethods(
            Class<?> ownerType, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
        return new BeanPropertyOnMethods(ownerType, name, readMethod, writeMethod);
    }

    static DeepToMapFunction getDeepToMapFunction(BeanOperator operator) {
        return operator.equals(BeanOperator.getDefault()) ?
                DeepToMapFunction.DEFAULT : new DeepToMapFunction(operator);
    }

    private static final class BeanClassImpl implements BeanClass {

        private final Class<?> type;
        private final @Immutable Map<String, BeanProperty> propertyMap;
        private final @Immutable Map<String, BeanProperty> readablePropertyMap;
        private final @Immutable Map<String, BeanProperty> writeablePropertyMap;

        private BeanClassImpl(Class<?> type, Map<String, BeanProperty> propertyMap) {
            this.type = type;
            this.propertyMap = MapKit.immutable(propertyMap);
            this.readablePropertyMap = MapKit.filter(this.propertyMap, e -> e.getValue().readable());
            this.writeablePropertyMap = MapKit.filter(this.propertyMap, e -> e.getValue().writeable());
        }

        @Override
        public Class<?> type() {
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
        public boolean readable() {
            return true;
        }

        @Override
        public boolean writeable() {
            return true;
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
        public @Nullable Object getValue(Object bean) throws UnsupportedOperationException {
            return FieldKit.getValue(field, bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            FieldKit.setValue(field, bean, value);
        }

        @Override
        public Class<?> ownerType() {
            return ownerType;
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

        private final @Nullable MethodInvoker getter;
        private final @Nullable MethodInvoker setter;

        private final @Nullable Field field;
        private final List<Annotation> fieldAnnotations;

        private BeanPropertyOnMethods(
                Class<?> ownerType, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
            this.ownerType = ownerType;
            this.name = name;

            if (readMethod == null) {
                Checker.checkState(writeMethod != null, "both read and write method are null");
                this.getter = null;
                this.setter = MethodInvoker.of(writeMethod);
                this.type = writeMethod.getParameterTypes()[0];
                this.genericType = writeMethod.getGenericParameterTypes()[0];
            } else {
                this.getter = MethodInvoker.of(readMethod);
                this.setter = writeMethod == null ? null : MethodInvoker.of(writeMethod);
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
        public boolean readable() {
            return getter != null;
        }

        @Override
        public boolean writeable() {
            return setter != null;
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
        public @Nullable Object getValue(Object bean) throws UnsupportedOperationException {
            if (!readable()) {
                throw new UnsupportedOperationException("property is not readable: " + name());
            }
            return getter.invoke(bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            if (!writeable()) {
                throw new UnsupportedOperationException("property is not writeable: " + name());
            }
            setter.invoke(bean, value);
        }

        @Override
        public Class<?> ownerType() {
            return ownerType;
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

    static final class DeepToMapFunction implements Function<Object, @Nullable Object> {

        private static final DeepToMapFunction DEFAULT = new DeepToMapFunction(BeanOperator.getDefault());

        private static final TypeRef<Map<String, Object>> MAP_TYPE = new TypeRef<Map<String, Object>>() {};

        private final BeanOperator operator;

        private DeepToMapFunction(BeanOperator operator) {
            this.operator = operator;
        }

        @Override
        @Nullable
        public Object apply(Object object) {
            if (object == null || !operator.canResolve(object)) {
                return object;
            }
            if (object instanceof List) {
                return ListKit.map((List<?>) object, this);
            }
            if (object instanceof Collection) {
                return SetKit.map((Collection<?>) object, this);
            }
            if (object instanceof Map) {
                return MapKit.map((Map<?, ?>) object, this, this);
            }
            Class<?> type = object.getClass();
            if (type.isArray()) {
                return ListKit.map(ArrayKit.toList(object), this);
            }
            return MapKit.map(operator.convert(object, MAP_TYPE), this, this);
        }
    }

    private static final class BeanMap implements Map<String, Object> {

        private final BeanClass beanClass;
        private final Object bean;

        private BeanMap(BeanClass beanClass, Object bean) {
            this.beanClass = beanClass;
            this.bean = bean;
        }

        @Override
        public int size() {
            return beanClass.getAllProperties().size();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            return beanClass.getAllProperties().containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public Object put(String key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ?> m) {

        }

        @Override
        public void clear() {

        }

        @NotNull
        @Override
        public Set<String> keySet() {
            return null;
        }

        @NotNull
        @Override
        public Collection<Object> values() {
            return null;
        }

        @NotNull
        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }
    }

    private static final class BeanViewMap implements Map<String, Object> {

        private final BeanClass

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public Object put(String key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ?> m) {

        }

        @Override
        public void clear() {

        }

        @NotNull
        @Override
        public Set<String> keySet() {
            return null;
        }

        @NotNull
        @Override
        public Collection<Object> values() {
            return null;
        }

        @NotNull
        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }
    }
}
