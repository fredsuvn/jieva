package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;
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
    public @Nullable void resolve(ResolveContext builder) {
        for (GekBeanResolver.Handler handler : handlers) {
            handler.resolve(builder);
            if (builder.isBreakResolving()) {
                break;
            }
        }
    }

    private GekBean resolve0(Type type) {
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
        private final Map<String, GekPropertyBase> properties = new LinkedHashMap<>();
        private boolean breakResolving = false;

        ContextImpl(Type type) {
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

        @Override
        public void breakResolving() {
            breakResolving = true;
        }

        @Override
        public boolean isBreakResolving() {
            return breakResolving;
        }

        private GekBean build() {
            return new BeanImpl(type, properties);
        }

        private static final class BeanImpl implements GekBean {

            private final Type type;
            private final Map<String, GekProperty> properties;
            private String toString = null;

            private BeanImpl(Type type, Map<String, GekPropertyBase> properties) {
                this.type = type;
                Map<String, GekProperty> props = new LinkedHashMap<>();
                properties.forEach((name, propBase) -> props.put(name, new PropertyImpl(propBase)));
                this.properties = Collections.unmodifiableMap(props);
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
                if (this == o) {
                    return true;
                }
                if (o == null) {
                    return false;
                }
                if (!Objects.equals(getClass(), o.getClass())) {
                    return false;
                }
                BeanImpl ob = Gek.as(o);
                return Objects.equals(getType(), ob.getType());
            }

            @Override
            public int hashCode() {
                return getType().hashCode();
            }

            @Override
            public String toString() {
                if (toString == null) {
                    String finalToString = GekBean.toString(this);
                    toString = finalToString;
                    return finalToString;
                }
                return toString;
            }

            private final class PropertyImpl implements GekProperty {

                private final GekPropertyBase propBase;
                private String toString = null;

                private PropertyImpl(GekPropertyBase propBase) {
                    this.propBase = propBase;
                }

                @Override
                public GekBean getOwner() {
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
                    PropertyImpl op = Gek.as(o);
                    return Objects.equals(getOwner(), op.getOwner()) && Objects.equals(getName(), op.getName());
                }

                @Override
                public int hashCode() {
                    return Objects.hash(getOwner().hashCode(), getName().hashCode());
                }

                @Override
                public String toString() {
                    if (toString == null) {
                        String finalToString = GekProperty.toString(this);
                        toString = finalToString;
                        return finalToString;
                    }
                    return toString;
                }
            }
        }
    }
}
