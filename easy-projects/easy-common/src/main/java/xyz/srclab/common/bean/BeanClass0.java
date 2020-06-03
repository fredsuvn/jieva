package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.FieldKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    static DeepToMapFunction newDeepToMapFunction(BeanClass beanClass) {
        return new DeepToMapFunction(beanClass);
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
        public @Nullable BeanProperty property(String propertyName) {
            return propertyMap.get(propertyName);
        }

        @Override
        public @Immutable Map<String, BeanProperty> properties() {
            return propertyMap;
        }

        @Override
        public @Immutable Map<String, BeanProperty> readableProperties() {
            return readablePropertyMap;
        }

        @Override
        public @Immutable Map<String, BeanProperty> writeableProperties() {
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
        public Field field() {
            return field;
        }

        @Override
        public @Immutable List<Annotation> fieldAnnotations() {
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
        public @Nullable Field field() {
            return field;
        }

        @Override
        public @Immutable List<Annotation> fieldAnnotations() {
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

    private static final class DeepToMapFunction implements Function<Object, @Nullable Object> {

        private final BeanClass beanClass;

        private DeepToMapFunction(BeanClass beanClass) {
            this.beanClass = beanClass;
        }

        @Override
        @Nullable
        public Object apply(Object object) {
            if (object == null || Defaults.isBasicType(object.getClass())) {
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
                return arrayToList(object);
            }
            return beanClass.deepToMap(object, this);
        }

        private List<?> arrayToList(Object array) {
            if (array instanceof boolean[]) {
                return ArrayKit.toList((boolean[]) array);
            }
            if (array instanceof byte[]) {
                return ArrayKit.toList((byte[]) array);
            }
            if (array instanceof short[]) {
                return ArrayKit.toList((short[]) array);
            }
            if (array instanceof char[]) {
                return ArrayKit.toList((char[]) array);
            }
            if (array instanceof int[]) {
                return ArrayKit.toList((int[]) array);
            }
            if (array instanceof long[]) {
                return ArrayKit.toList((long[]) array);
            }
            if (array instanceof float[]) {
                return ArrayKit.toList((float[]) array);
            }
            if (array instanceof double[]) {
                return ArrayKit.toList((double[]) array);
            }
            Object[] a = (Object[]) array;
            return ListKit.map(a, this);
        }
    }
}
