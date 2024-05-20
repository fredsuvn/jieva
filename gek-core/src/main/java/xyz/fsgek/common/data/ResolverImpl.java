package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekFlag;
import xyz.fsgek.common.cache.GekCache;
import xyz.fsgek.common.collect.GekColl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

final class ResolverImpl implements GekDataResolver, GekDataResolver.Handler {

    private final List<GekDataResolver.Handler> handlers;
    private final @Nullable GekCache<Type, GekDataDescriptor> cache;

    ResolverImpl(Iterable<GekDataResolver.Handler> handlers, @Nullable GekCache<Type, GekDataDescriptor> cache) {
        this.handlers = GekColl.toList(handlers);
        this.cache = cache;
    }

    @Override
    public GekDataDescriptor resolve(Type type) {
        if (cache == null) {
            return resolve0(type);
        }
        return cache.get(type, this::resolve0);
    }

    @Override
    public List<GekDataResolver.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public GekDataResolver withFirstHandler(Handler handler) {
        List<GekDataResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new ResolverImpl(newHandlers, cache);
    }

    @Override
    public GekDataResolver withLastHandler(Handler handler) {
        List<GekDataResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new ResolverImpl(newHandlers, cache);
    }

    @Override
    public GekDataResolver.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable GekFlag resolve(GekDataResolver.Context builder) {
        for (GekDataResolver.Handler handler : handlers) {
            GekFlag flag = handler.resolve(builder);
            if (Objects.equals(flag, GekFlag.BREAK)) {
                return GekFlag.BREAK;
            }
        }
        return null;
    }

    private GekDataDescriptor resolve0(Type type) {
        Context builder = new Context(type);
        for (Handler handler : handlers) {
            GekFlag flag = handler.resolve(builder);
            if (Objects.equals(flag, GekFlag.BREAK)) {
                break;
            }
        }
        return builder.build();
    }

    static final class Context implements GekDataResolver.Context {

        private final Type type;
        private final Map<String, GekPropertyBase> properties = new LinkedHashMap<>();

        Context(Type type) {
            this.type = type;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, GekPropertyBase> getProperties() {
            return properties;
        }

        private GekDataDescriptor build() {
            return new DataDescriptorImpl(type, properties);
        }
    }

    private static final class DataDescriptorImpl implements GekDataDescriptor {

        private final Type type;
        private final Map<String, GekPropertyDescriptor> properties;

        private DataDescriptorImpl(Type type, Map<String, GekPropertyBase> properties) {
            this.type = type;
            this.properties = GekColl.toMap(properties, name -> name, PropertyDescriptorImpl::new);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, GekPropertyDescriptor> getProperties() {
            return properties;
        }

        @Override
        public boolean equals(Object o) {
            return GekDataDescriptor.equals(this, o);
        }

        @Override
        public int hashCode() {
            return GekDataDescriptor.hashCode(this);
        }

        @Override
        public String toString() {
            return GekDataDescriptor.toString(this);
        }

        private final class PropertyDescriptorImpl implements GekPropertyDescriptor {

            private final GekPropertyBase base;

            private PropertyDescriptorImpl(GekPropertyBase propBase) {
                this.base = propBase;
            }

            @Override
            public GekDataDescriptor getOwner() {
                return DataDescriptorImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            @Nullable
            public Object getValue(Object data) {
                return base.getValue(data);
            }

            @Override
            public void setValue(Object data, @Nullable Object value) {
                base.setValue(data, value);
            }

            @Override
            public Type getType() {
                return base.getType();
            }

            @Override
            public Class<?> getRawType() {
                return base.getRawType();
            }

            @Override
            public @Nullable Method getGetter() {
                return base.getGetter();
            }

            @Override
            public @Nullable Method getSetter() {
                return base.getSetter();
            }

            @Override
            public @Nullable Field getField() {
                return base.getField();
            }

            @Override
            public List<Annotation> getGetterAnnotations() {
                return base.getGetterAnnotations();
            }

            @Override
            public List<Annotation> getSetterAnnotations() {
                return base.getSetterAnnotations();
            }

            @Override
            public List<Annotation> getFieldAnnotations() {
                return base.getFieldAnnotations();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return base.getAnnotations();
            }

            @Override
            public @Nullable Annotation getAnnotation(Class<?> annotationType) {
                return base.getAnnotation(annotationType);
            }

            @Override
            public boolean isReadable() {
                return base.isReadable();
            }

            @Override
            public boolean isWriteable() {
                return base.isWriteable();
            }

            @Override
            public boolean equals(Object o) {
                return o != null && Objects.equals(getClass(), o.getClass())
                    && GekPropertyDescriptor.equals(this, o);
            }

            @Override
            public int hashCode() {
                return GekPropertyDescriptor.hashCode(this);
            }

            @Override
            public String toString() {
                return GekPropertyDescriptor.toString(this);
            }
        }
    }
}
