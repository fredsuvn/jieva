package xyz.srclab.common.record;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.FieldKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author sunqian
 */
final class Resolve0 {

    static ResolveHandler getFieldHandler() {
        return FieldHandler.INSTANCE;
    }

    static ResolveHandler getBeanPatternHandler() {
        return BeanPatternHandler.INSTANCE;
    }

    static ResolveHandler getNamingPatternHandler() {
        return NamingPatternHandler.INSTANCE;
    }

    static RecordEntry newEntryOnField(Field field) {
        return new EntryOnField(field);
    }

    static RecordEntry newEntryOnMethods(
            String name, @Nullable Method readMethod, @Nullable Method writeMethod, Class<?> owner) {
        return new EntryOnMethods(name, readMethod, writeMethod, owner);
    }

    private static abstract class RecordEntryImplBase implements RecordEntry {

        @Override
        public int hashCode() {
            return getKey().hashCode();
        }

        @Override
        public boolean equals(@Nullable Object that) {
            if (this == that) {
                return true;
            }
            if (!(that instanceof RecordEntry)) {
                return false;
            }
            return getOwner().equals(((RecordEntry) that).getOwner())
                    && getKey().equals(((RecordEntry) that).getKey());
        }

        @Override
        public String toString() {
            return getGenericType().getTypeName() + " "
                    + getOwner().getTypeName() + "."
                    + getKey();
        }
    }

    private static final class EntryOnField extends RecordEntryImplBase {

        private final Field field;
        private final List<Annotation> fieldAnnotations;

        private EntryOnField(Field field) {
            this.field = field;
            this.fieldAnnotations = ListKit.immutable(this.field.getAnnotations());
        }

        @Override
        public String getKey() {
            return field.getName();
        }

        @Override
        public Class<?> getType() {
            return field.getType();
        }

        @Override
        public Type getGenericType() {
            return field.getGenericType();
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWriteable() {
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
        public Field getField() {
            return field;
        }

        @Override
        public @Immutable List<Annotation> getFieldAnnotations() {
            return fieldAnnotations;
        }

        @Override
        public Class<?> getOwner() {
            return field.getDeclaringClass();
        }
    }

    private static final class EntryOnMethods extends RecordEntryImplBase {

        private final String name;
        private final Class<?> type;
        private final Type genericType;
        private final Class<?> owner;

        private final @Nullable Method readMethod;
        private final @Nullable Method writeMethod;
        private @Nullable MethodInvoker getter;
        private @Nullable MethodInvoker setter;

        private final @Nullable Field field;
        private final List<Annotation> fieldAnnotations;

        private EntryOnMethods(String name, @Nullable Method readMethod, @Nullable Method writeMethod, Class<?> owner) {
            this.name = name;
            this.owner = owner;
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;

            if (readMethod == null) {
                Checker.checkState(writeMethod != null, "Both read and write method are null");
                this.type = writeMethod.getParameterTypes()[0];
                this.genericType = writeMethod.getGenericParameterTypes()[0];
            } else {
                this.type = readMethod.getReturnType();
                this.genericType = readMethod.getGenericReturnType();
            }

            this.field = FieldKit.getDeclaredField(owner, name);
            this.fieldAnnotations = field == null ? Collections.emptyList() :
                    ListKit.immutable(field.getAnnotations());
        }

        @Override
        public String getKey() {
            return name;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        @Override
        public Type getGenericType() {
            return genericType;
        }

        @Override
        public boolean isReadable() {
            return readMethod != null;
        }

        @Override
        public boolean isWriteable() {
            return writeMethod != null;
        }

        @Override
        public @Nullable Object getValue(Object bean) throws UnsupportedOperationException {
            if (!isReadable()) {
                throw new UnsupportedOperationException("Entry is not readable: " + getKey());
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
            if (!isWriteable()) {
                throw new UnsupportedOperationException("Entry is not writeable: " + getKey());
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
        public @Nullable Field getField() {
            return field;
        }

        @Override
        public @Immutable List<Annotation> getFieldAnnotations() {
            return fieldAnnotations;
        }

        @Override
        public Class<?> getOwner() {
            return owner;
        }
    }

    private static final class FieldHandler implements ResolveHandler {

        public static final FieldHandler INSTANCE = new FieldHandler();

        @Override
        public void resolve(Class<?> recordClass, Context context) {
            Map<String, RecordEntry> entryMap = context.entryMap();
            for (Field field : context.fields()) {
                if (entryMap.containsKey(field.getName())) {
                    continue;
                }
                entryMap.put(field.getName(), RecordEntry.newEntryOnField(field));
            }
        }
    }

    private static abstract class MethodHandler implements ResolveHandler {

        @Override
        public void resolve(Class<?> recordClass, Context context) {

            final class EntryBuilder {

                private final String key;
                private @Nullable Method readMethod;
                private @Nullable Method writeMethod;

                private EntryBuilder(String key) {
                    this.key = key;
                }

                public String getKey() {
                    return key;
                }

                @Nullable
                public Method getReadMethod() {
                    return readMethod;
                }

                public void setReadMethod(Method readMethod) {
                    this.readMethod = readMethod;
                }

                @Nullable
                public Method getWriteMethod() {
                    return writeMethod;
                }

                public void setWriteMethod(Method writeMethod) {
                    this.writeMethod = writeMethod;
                }

                public RecordEntry build() {
                    return RecordEntry.newEntryOnMethods(key, readMethod, writeMethod, recordClass);
                }
            }

            Map<String, RecordEntry> entryMap = context.entryMap();
            Set<KeyOnMethod> getters = new LinkedHashSet<>();
            Set<KeyOnMethod> setters = new LinkedHashSet<>();
            List<Method> methods = context.methods();
            findAccessor(methods, getters, setters);

            Map<String, EntryBuilder> builderMap = new LinkedHashMap<>();
            getters.forEach(keyOnMethod -> {
                EntryBuilder builder = builderMap.computeIfAbsent(
                        keyOnMethod.getKey(),
                        EntryBuilder::new);
                builder.setReadMethod(keyOnMethod.getMethod());
            });
            setters.forEach(keyOnMethod -> {
                EntryBuilder builder = builderMap.computeIfAbsent(
                        keyOnMethod.getKey(),
                        EntryBuilder::new);
                if (builder.getWriteMethod() != null) {
                    return;
                }
                Method writeMethod = keyOnMethod.getMethod();
                @Nullable Method readMethod = builder.getReadMethod();
                if (readMethod == null) {
                    builder.setWriteMethod(writeMethod);
                    return;
                }
                if (readMethod.getGenericReturnType().equals(writeMethod.getGenericParameterTypes()[0])) {
                    builder.setWriteMethod(writeMethod);
                }
            });

            builderMap.forEach((name, builder) -> entryMap.put(name, builder.build()));
        }

        protected abstract void findAccessor(
                List<Method> methods, @Out Set<KeyOnMethod> getters, @Out Set<KeyOnMethod> setters);

        private static final class KeyOnMethod {

            private final String key;
            private final Method method;

            private KeyOnMethod(String key, Method method) {
                this.key = key;
                this.method = method;
            }

            public String getKey() {
                return key;
            }

            public Method getMethod() {
                return method;
            }
        }
    }

    private static final class BeanPatternHandler extends MethodHandler {

        public static final BeanPatternHandler INSTANCE = new BeanPatternHandler();

        @Override
        protected void findAccessor(
                List<Method> methods, @Out Set<KeyOnMethod> getters, @Out Set<KeyOnMethod> setters) {
            for (Method method : methods) {
                String name = method.getName();
                if (method.getParameterCount() == 0) {
                    if (name.startsWith("get") && name.length() > 3) {
                        getters.add(new KeyOnMethod(StringUtils.uncapitalize(name.substring(3)), method));
                        continue;
                    }
                    if (name.startsWith("is") && name.length() > 2) {
                        getters.add(new KeyOnMethod(StringUtils.uncapitalize(name.substring(2)), method));
                        continue;
                    }
                }
                if (method.getParameterCount() == 1) {
                    if (name.startsWith("set") && name.length() > 3) {
                        getters.add(new KeyOnMethod(StringUtils.uncapitalize(name.substring(3)), method));
                    }
                }
            }
        }
    }

    private static final class NamingPatternHandler extends MethodHandler {

        public static final NamingPatternHandler INSTANCE = new NamingPatternHandler();

        @Override
        protected void findAccessor(
                List<Method> methods, @Out Set<MethodHandler.KeyOnMethod> getters, @Out Set<MethodHandler.KeyOnMethod> setters) {
            for (Method method : methods) {
                if (method.getParameterCount() == 0) {
                    getters.add(new KeyOnMethod(method.getName(), method));
                    continue;
                }
                if (method.getParameterCount() == 1) {
                    getters.add(new KeyOnMethod(method.getName(), method));
                }
            }
        }
    }
}
