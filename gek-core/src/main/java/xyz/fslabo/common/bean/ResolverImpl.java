package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.handlers.JavaBeanResolverHandler;
import xyz.fslabo.common.collect.JieColl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

final class ResolverImpl implements BeanResolver, BeanResolver.Handler {

    static ResolverImpl DEFAULT_RESOLVER =
        new ResolverImpl(Collections.singletonList(JavaBeanResolverHandler.INSTANCE));

    private final List<BeanResolver.Handler> handlers;

    ResolverImpl(Iterable<BeanResolver.Handler> handlers) {
        this.handlers = JieColl.toList(handlers);
    }

    @Override
    public BeanInfo resolve(Type type) throws BeanResolvingException {
        try {
            Context builder = new Context(type);
            for (Handler handler : handlers) {
                Flag flag = handler.resolve(builder);
                if (Objects.equals(flag, Flag.BREAK)) {
                    break;
                }
            }
            return builder.build();
        } catch (BeanResolvingException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanResolvingException(type, e);
        }
    }

    @Override
    public List<BeanResolver.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public BeanResolver withFirstHandler(Handler handler) {
        List<BeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new ResolverImpl(newHandlers);
    }

    @Override
    public BeanResolver withLastHandler(Handler handler) {
        List<BeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new ResolverImpl(newHandlers);
    }

    @Override
    public BeanResolver.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Flag resolve(BeanResolver.Context builder) {
        for (BeanResolver.Handler handler : handlers) {
            Flag flag = handler.resolve(builder);
            if (Objects.equals(flag, Flag.BREAK)) {
                return Flag.BREAK;
            }
        }
        return null;
    }

    static final class Context implements BeanResolver.Context {

        private final Type type;
        private final Map<String, BeanPropertyBase> properties = new LinkedHashMap<>();
        private final List<BeanMethodBase> methods = new LinkedList<>();

        Context(Type type) {
            this.type = type;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, BeanPropertyBase> getProperties() {
            return properties;
        }

        @Override
        public List<BeanMethodBase> getMethods() {
            return methods;
        }

        private BeanInfo build() {
            return new BeanInfoImpl(type, properties, methods);
        }
    }

    private static final class BeanInfoImpl implements BeanInfo {

        private final Type type;
        private final Map<String, BeanProperty> properties;
        private final List<BeanMethod> methods;

        private BeanInfoImpl(Type type, Map<String, BeanPropertyBase> properties, List<BeanMethodBase> methods) {
            this.type = type;
            this.properties = JieColl.toMap(properties, name -> name, BeanPropertyImpl::new);
            this.methods = JieColl.toList(methods, BeanMethodImpl::new);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, BeanProperty> getProperties() {
            return properties;
        }

        @Override
        public List<BeanMethod> getMethods() {
            return methods;
        }

        @Override
        public boolean equals(Object o) {
            return JieBean.equals(this, o);
        }

        @Override
        public int hashCode() {
            return JieBean.hashCode(this);
        }

        @Override
        public String toString() {
            return JieBean.toString(this);
        }

        private final class BeanPropertyImpl implements BeanProperty {

            private final BeanPropertyBase base;

            private BeanPropertyImpl(BeanPropertyBase propBase) {
                this.base = propBase;
            }

            @Override
            public BeanInfo getOwner() {
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
            public @Nullable java.lang.reflect.Method getGetter() {
                return base.getGetter();
            }

            @Override
            public @Nullable java.lang.reflect.Method getSetter() {
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
                    && JieBean.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieBean.hashCode(this);
            }

            @Override
            public String toString() {
                return JieBean.toString(this);
            }
        }

        private final class BeanMethodImpl implements BeanMethod {

            private final BeanMethodBase base;

            private BeanMethodImpl(BeanMethodBase propBase) {
                this.base = propBase;
            }

            @Override
            public BeanInfo getOwner() {
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
            public java.lang.reflect.Method getMethod() {
                return base.getMethod();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return base.getAnnotations();
            }
        }
    }
}
