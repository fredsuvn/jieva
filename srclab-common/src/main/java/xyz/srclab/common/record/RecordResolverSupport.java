package xyz.srclab.common.record;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.collection.MapOps;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author sunqian
 */
final class RecordResolverSupport {

    static RecordResolver defaultResolver() {
        return RecordResolverHolder.INSTANCE;
    }

    static RecordResolverHandler getBeanPatternHandler() {
        return BeanPatternHandler.INSTANCE;
    }

    static RecordResolverHandler getNamingPatternHandler() {
        return NamingPatternHandler.INSTANCE;
    }

    static RecordType newRecordType(Type type, Map<String, RecordEntry> entryMap) {
        return new RecordTypeImpl(type, entryMap);
    }

    static RecordEntry newEntry(
            Type owner, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
        return new EntryOnMethods(owner, name, readMethod, writeMethod);
    }

    private static final class RecordTypeImpl implements RecordType {

        private final Class<?> type;
        private final Type genericType;
        private final @Immutable Map<String, RecordEntry> entryMap;

        private RecordTypeImpl(Type type, Map<String, RecordEntry> entryMap) {
            this.type = TypeKit.getRawType(type);
            this.genericType = type;
            this.entryMap = MapOps.immutable(entryMap);
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
        public @Immutable Map<String, RecordEntry> entryMap() {
            return entryMap;
        }

        @Override
        public int hashCode() {
            return genericType().hashCode();
        }

        @Override
        public boolean equals(@Nullable Object that) {
            if (this == that) {
                return true;
            }
            if (!(that instanceof RecordType)) {
                return false;
            }
            return genericType().equals(((RecordType) that).genericType());
        }

        @Override
        public String toString() {
            return "record " + genericType().getTypeName();
        }
    }

    private static abstract class EntryImplBase implements RecordEntry {

        @Override
        public int hashCode() {
            return key().hashCode();
        }

        @Override
        public boolean equals(@Nullable Object that) {
            if (this == that) {
                return true;
            }
            if (!(that instanceof RecordEntry)) {
                return false;
            }
            return genericOwnerType().equals(((RecordEntry) that).genericOwnerType())
                    && key().equals(((RecordEntry) that).key());
        }

        @Override
        public String toString() {
            return genericType().getTypeName() + " "
                    + genericOwnerType().getTypeName() + "."
                    + key();
        }
    }

    private static final class EntryOnMethods extends EntryImplBase {

        private final Class<?> ownerType;
        private final Type owner;
        private final String name;
        private final Class<?> type;
        private final Type genericType;

        private final @Nullable Method readMethod;
        private final @Nullable Method writeMethod;
        private @Nullable MethodInvoker getter;
        private @Nullable MethodInvoker setter;

        private final @Nullable Field field;
        private final List<Annotation> fieldAnnotations;

        private EntryOnMethods(Type owner, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
            this.ownerType = TypeKit.getRawType(owner);
            this.owner = owner;
            this.name = name;
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;

            if (readMethod == null) {
                Check.checkState(writeMethod != null, "Both read and write method are null");
                Type type = TypeKit.tryActualType(
                        writeMethod.getGenericParameterTypes()[0], owner, writeMethod.getDeclaringClass());
                this.type = TypeKit.getRawType(type);
                this.genericType = type;
            } else {
                Type type = TypeKit.tryActualType(
                        readMethod.getGenericReturnType(), owner, readMethod.getDeclaringClass());
                this.type = TypeKit.getRawType(type);
                this.genericType = type;
            }

            this.field = FieldKit.findDeclaredField(TypeKit.getRawType(owner), name);
            this.fieldAnnotations = field == null ? Collections.emptyList() :
                    ListOps.immutable(field.getAnnotations());
        }

        @Override
        public Class<?> ownerType() {
            return ownerType;
        }

        @Override
        public Type genericOwnerType() {
            return owner;
        }

        @Override
        public String key() {
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
            return readMethod != null;
        }

        @Override
        public boolean writeable() {
            return writeMethod != null;
        }

        @Override
        public @Nullable Object getValue(Object record) throws UnsupportedOperationException {
            if (!readable()) {
                throw new UnsupportedOperationException("Entry is not readable: " + key());
            }
            if (getter == null) {
                synchronized (readMethod) {
                    if (getter == null) {
                        getter = MethodInvoker.of(readMethod);
                    }
                }
            }
            return getter.invoke(record);
        }

        @Override
        public void setValue(Object record, @Nullable Object value) throws UnsupportedOperationException {
            if (!writeable()) {
                throw new UnsupportedOperationException("Entry is not writeable: " + key());
            }
            if (setter == null) {
                synchronized (writeMethod) {
                    if (setter == null) {
                        setter = MethodInvoker.of(writeMethod);
                    }
                }
            }
            setter.invoke(record, value);
        }

        @Override
        public @Nullable Field getField() {
            return field;
        }

        @Override
        public @Immutable List<Annotation> getFieldAnnotations() {
            return fieldAnnotations;
        }
    }

    private static abstract class MethodHandler implements RecordResolverHandler {

        @Override
        public void resolve(Type recordType, Context context) {

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
                    return RecordEntry.newEntry(recordType, key, readMethod, writeMethod);
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
                if (method.isBridge() || Record.class.equals(method.getDeclaringClass())) {
                    continue;
                }
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
                if (method.isBridge() || Record.class.equals(method.getDeclaringClass())) {
                    continue;
                }
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

    private static final class RecordResolverHolder {

        public static final RecordResolver INSTANCE = RecordResolver.newBuilder()
                .handler(RecordResolverHandler.getBeanPatternHandler())
                .build();
    }
}
