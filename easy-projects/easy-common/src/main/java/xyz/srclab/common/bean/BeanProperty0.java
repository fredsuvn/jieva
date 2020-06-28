package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.FieldKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author sunqian
 */
final class BeanProperty0 {

    static BeanProperty newBeanPropertyOnField(Class<?> ownerType, Field field) {
        return new BeanPropertyOnField(ownerType, field);
    }

    static BeanProperty newBeanPropertyOnMethods(
            Class<?> ownerType, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
        return new BeanPropertyOnMethods(ownerType, name, readMethod, writeMethod);
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
                Check.checkState(writeMethod != null, "both read and write method are null");
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
}
