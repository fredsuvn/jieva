package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.cache.FsCache;
import xyz.fsgek.common.collect.FsCollect;
import xyz.fsgek.common.base.Fs;
import xyz.fsgek.common.bean.handlers.JavaBeanResolveHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

final class BeanResolverImpl implements FsBeanResolver, FsBeanResolver.Handler {

    static final BeanResolverImpl INSTANCE =
        new BeanResolverImpl(Collections.singletonList(JavaBeanResolveHandler.INSTANCE), FsCache.softCache());

    private final List<FsBeanResolver.Handler> handlers;
    private final @Nullable FsCache<Type, FsBean> cache;

    BeanResolverImpl(Iterable<FsBeanResolver.Handler> handlers, @Nullable FsCache<Type, FsBean> cache) {
        this.handlers = FsCollect.immutableList(handlers);
        this.cache = cache;
    }

    @Override
    public FsBean resolve(Type type) {
        if (cache == null) {
            return resolve0(type);
        }
        return cache.get(type, this::resolve0);
    }

    @Override
    public List<FsBeanResolver.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public FsBeanResolver insertHandler(int index, FsBeanResolver.Handler handler) {
        List<FsBeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(index, handler);
        return new BeanResolverImpl(newHandlers, cache);
    }

    @Override
    public FsBeanResolver.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable void resolve(ResolveContext builder) {
        for (FsBeanResolver.Handler handler : handlers) {
            handler.resolve(builder);
            if (builder.isBreakResolving()) {
                break;
            }
        }
    }

    private FsBean resolve0(Type type) {
        ContextImpl builder = new ContextImpl(type);
        for (Handler handler : handlers) {
            handler.resolve(builder);
            if (builder.isBreakResolving()) {
                break;
            }
        }
        return builder.build();
    }

    static final class ContextImpl implements ResolveContext {

        private final Type type;
        private final Map<String, FsPropertyBase> properties = new LinkedHashMap<>();
        private boolean breakResolving = false;

        ContextImpl(Type type) {
            this.type = type;
        }

        @Override
        public Type beanType() {
            return type;
        }

        @Override
        public Map<String, FsPropertyBase> beanProperties() {
            return properties;
        }

        @Override
        public void breakResolving() {
            breakResolving = true;
        }

        @Override
        public boolean isBreakResolving() {
            return breakResolving;
        }

        private FsBean build() {
            return new BeanImpl(type, properties);
        }

        private static final class BeanImpl implements FsBean {

            private final Type type;
            private final Map<String, FsProperty> properties;
            private String toString = null;

            private BeanImpl(Type type, Map<String, FsPropertyBase> properties) {
                this.type = type;
                Map<String, FsProperty> props = new LinkedHashMap<>();
                properties.forEach((name, propBase) -> props.put(name, new PropertyImpl(propBase)));
                this.properties = Collections.unmodifiableMap(props);
            }

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public Map<String, FsProperty> getProperties() {
                return properties;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null) {
                    return false;
                }
                if (!Objects.equals(getClass(), o.getClass())) {
                    return false;
                }
                BeanImpl ob = Fs.as(o);
                return Objects.equals(getType(), ob.getType());
            }

            @Override
            public int hashCode() {
                return getType().hashCode();
            }

            @Override
            public String toString() {
                if (toString == null) {
                    String finalToString = FsBean.toString(this);
                    toString = finalToString;
                    return finalToString;
                }
                return toString;
            }

            private final class PropertyImpl implements FsProperty {

                private final FsPropertyBase propBase;
                private String toString = null;

                private PropertyImpl(FsPropertyBase propBase) {
                    this.propBase = propBase;
                }

                @Override
                public FsBean getOwner() {
                    return BeanImpl.this;
                }

                @Override
                public String getName() {
                    return propBase.getName();
                }

                @Override
                @Nullable
                public Object get(Object bean) {
                    return propBase.get(bean);
                }

                @Override
                public void set(Object bean, @Nullable Object value) {
                    propBase.set(bean, value);
                }

                @Override
                public Type getType() {
                    return propBase.getType();
                }

                @Override
                public Class<?> getRawType() {
                    return propBase.getRawType();
                }

                @Override
                public @Nullable Method getGetter() {
                    return propBase.getGetter();
                }

                @Override
                public @Nullable Method getSetter() {
                    return propBase.getSetter();
                }

                @Override
                public @Nullable Field getField() {
                    return propBase.getField();
                }

                @Override
                public List<Annotation> getGetterAnnotations() {
                    return propBase.getGetterAnnotations();
                }

                @Override
                public List<Annotation> getSetterAnnotations() {
                    return propBase.getSetterAnnotations();
                }

                @Override
                public List<Annotation> getFieldAnnotations() {
                    return propBase.getFieldAnnotations();
                }

                @Override
                public List<Annotation> getAnnotations() {
                    return propBase.getAnnotations();
                }

                @Override
                public @Nullable Annotation getAnnotation(Class<?> annotationType) {
                    return propBase.getAnnotation(annotationType);
                }

                @Override
                public boolean isReadable() {
                    return propBase.isReadable();
                }

                @Override
                public boolean isWriteable() {
                    return propBase.isWriteable();
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (o == null) {
                        return false;
                    }
                    if (!Objects.equals(getClass(), o.getClass())) {
                        return false;
                    }
                    PropertyImpl op = Fs.as(o);
                    return Objects.equals(getOwner(), op.getOwner()) && Objects.equals(getName(), op.getName());
                }

                @Override
                public int hashCode() {
                    return Objects.hash(getOwner().hashCode(), getName().hashCode());
                }

                @Override
                public String toString() {
                    if (toString == null) {
                        String finalToString = FsProperty.toString(this);
                        toString = finalToString;
                        return finalToString;
                    }
                    return toString;
                }
            }
        }
    }
}
