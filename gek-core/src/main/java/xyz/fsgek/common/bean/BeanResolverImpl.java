package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekFlag;
import xyz.fsgek.common.bean.handlers.JavaBeanResolveHandler;
import xyz.fsgek.common.cache.GekCache;
import xyz.fsgek.common.collect.GekColl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

final class BeanResolverImpl implements GekBeanResolver, GekBeanResolver.Handler {

    static final BeanResolverImpl INSTANCE =
        new BeanResolverImpl(Collections.singletonList(JavaBeanResolveHandler.INSTANCE), GekCache.softCache());

    private final List<GekBeanResolver.Handler> handlers;
    private final @Nullable GekCache<Type, GekBean> cache;

    BeanResolverImpl(Iterable<GekBeanResolver.Handler> handlers, @Nullable GekCache<Type, GekBean> cache) {
        this.handlers = GekColl.toList(handlers);
        this.cache = cache;
    }

    @Override
    public GekBean resolve(Type type) {
        if (cache == null) {
            return resolve0(type);
        }
        return cache.get(type, this::resolve0);
    }

    @Override
    public List<GekBeanResolver.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public GekBeanResolver insertHandler(int index, GekBeanResolver.Handler handler) {
        List<GekBeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(index, handler);
        return new BeanResolverImpl(newHandlers, cache);
    }

    @Override
    public GekBeanResolver.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable GekFlag resolve(ResolveContext builder) {
        for (GekBeanResolver.Handler handler : handlers) {
            GekFlag flag = handler.resolve(builder);
            if (Objects.equals(flag, GekFlag.BREAK)) {
                return GekFlag.BREAK;
            }
        }
        return null;
    }

    private GekBean resolve0(Type type) {
        Context builder = new Context(type);
        for (Handler handler : handlers) {
            GekFlag flag = handler.resolve(builder);
            if (Objects.equals(flag, GekFlag.BREAK)) {
                break;
            }
        }
        return builder.build();
    }

    static final class Context implements ResolveContext {

        private final Type type;
        private final Map<String, GekPropertyBase> properties = new LinkedHashMap<>();

        Context(Type type) {
            this.type = type;
        }

        @Override
        public Type beanType() {
            return type;
        }

        @Override
        public Map<String, GekPropertyBase> beanProperties() {
            return properties;
        }

        private GekBean build() {
            return new BeanImpl(type, properties);
        }
    }

    private static final class BeanImpl implements GekBean {

        private final Type type;
        private final Map<String, GekProperty> properties;

        private BeanImpl(Type type, Map<String, GekPropertyBase> properties) {
            this.type = type;
            this.properties = GekColl.toMap(properties, name -> name, PropertyImpl::new);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, GekProperty> getProperties() {
            return properties;
        }

        @Override
        public boolean equals(Object o) {
            return GekBean.equals(this, o);
        }

        @Override
        public int hashCode() {
            return GekBean.hashCode(this);
        }

        @Override
        public String toString() {
            return GekBean.toString(this);
        }

        private final class PropertyImpl implements GekProperty {

            private final GekPropertyBase base;

            private PropertyImpl(GekPropertyBase propBase) {
                this.base = propBase;
            }

            @Override
            public GekBean getOwner() {
                return BeanImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            @Nullable
            public Object get(Object bean) {
                return base.get(bean);
            }

            @Override
            public void set(Object bean, @Nullable Object value) {
                base.set(bean, value);
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
                return GekProperty.equals(this, o);
            }

            @Override
            public int hashCode() {
                return GekProperty.hashCode(this);
            }

            @Override
            public String toString() {
                return GekProperty.toString(this);
            }
        }
    }
}
