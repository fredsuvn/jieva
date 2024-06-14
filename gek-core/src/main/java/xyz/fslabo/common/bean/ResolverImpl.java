package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.handlers.JavaBeanResolverHandler;
import xyz.fslabo.common.collect.GekColl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

final class ResolverImpl implements GekBeanResolver, GekBeanResolver.Handler {

    static ResolverImpl DEFAULT_RESOLVER =
        new ResolverImpl(Collections.singletonList(JavaBeanResolverHandler.INSTANCE));

    private final List<GekBeanResolver.Handler> handlers;

    ResolverImpl(Iterable<GekBeanResolver.Handler> handlers) {
        this.handlers = GekColl.toList(handlers);
    }

    @Override
    public GekBeanInfo resolve(Type type) {
        Context builder = new Context(type);
        for (Handler handler : handlers) {
            Flag flag = handler.resolve(builder);
            if (Objects.equals(flag, Flag.BREAK)) {
                break;
            }
        }
        return builder.build();
    }

    @Override
    public List<GekBeanResolver.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public GekBeanResolver withFirstHandler(Handler handler) {
        List<GekBeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new ResolverImpl(newHandlers);
    }

    @Override
    public GekBeanResolver withLastHandler(Handler handler) {
        List<GekBeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new ResolverImpl(newHandlers);
    }

    @Override
    public GekBeanResolver.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Flag resolve(GekBeanResolver.Context builder) {
        for (GekBeanResolver.Handler handler : handlers) {
            Flag flag = handler.resolve(builder);
            if (Objects.equals(flag, Flag.BREAK)) {
                return Flag.BREAK;
            }
        }
        return null;
    }

    static final class Context implements GekBeanResolver.Context {

        private final Type type;
        private final Map<String, GekPropertyBase> properties = new LinkedHashMap<>();
        private final List<GekMethodBase> methods = new LinkedList<>();

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

        @Override
        public List<GekMethodBase> getMethods() {
            return methods;
        }

        private GekBeanInfo build() {
            return new BeanInfoImpl(type, properties, methods);
        }
    }

    private static final class BeanInfoImpl implements GekBeanInfo {

        private final Type type;
        private final Map<String, GekPropertyInfo> properties;
        private final List<GekMethodInfo> methods;

        private BeanInfoImpl(Type type, Map<String, GekPropertyBase> properties, List<GekMethodBase> methods) {
            this.type = type;
            this.properties = GekColl.toMap(properties, name -> name, PropertyInfoImpl::new);
            this.methods = GekColl.toList(methods, MethodInfoImpl::new);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, GekPropertyInfo> getProperties() {
            return properties;
        }

        @Override
        public List<GekMethodInfo> getMethods() {
            return methods;
        }

        @Override
        public boolean equals(Object o) {
            return GekBeanInfo.equals(this, o);
        }

        @Override
        public int hashCode() {
            return GekBeanInfo.hashCode(this);
        }

        @Override
        public String toString() {
            return GekBeanInfo.toString(this);
        }

        private final class PropertyInfoImpl implements GekPropertyInfo {

            private final GekPropertyBase base;

            private PropertyInfoImpl(GekPropertyBase propBase) {
                this.base = propBase;
            }

            @Override
            public GekBeanInfo getOwner() {
                return BeanInfoImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            @Nullable
            public Object getValue(Object bean) {
                return base.getValue(bean);
            }

            @Override
            public void setValue(Object bean, @Nullable Object value) {
                base.setValue(bean, value);
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
                    && GekPropertyInfo.equals(this, o);
            }

            @Override
            public int hashCode() {
                return GekPropertyInfo.hashCode(this);
            }

            @Override
            public String toString() {
                return GekPropertyInfo.toString(this);
            }
        }

        private final class MethodInfoImpl implements GekMethodInfo {

            private final GekMethodBase base;

            private MethodInfoImpl(GekMethodBase propBase) {
                this.base = propBase;
            }

            @Override
            public GekBeanInfo getOwner() {
                return BeanInfoImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            public Object invoke(Object bean, Object... args) {
                return base.invoke(bean, args);
            }

            @Override
            public Method getMethod() {
                return base.getMethod();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return base.getAnnotations();
            }
        }
    }
}
