package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.pattern.builder.HandlersBuilder;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.reflect.MethodKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BeanResolverBuilder extends HandlersBuilder<BeanResolver, BeanResolverHandler, BeanResolverBuilder> {

    private boolean useCache = true;

    public BeanResolverBuilder useCache(boolean useCache) {
        this.useCache = useCache;
        this.updateState();
        return this;
    }

    @Override
    protected BeanResolver buildNew() {
        if (handlers.isEmpty()) {
            throw new IllegalArgumentException("There is no handler added");
        }
        BeanResolver resolver = new BeanResolverImpl(handlers);
        return useCache ? new CachedBeanResolver(resolver) : resolver;
    }

    private static final class CachedBeanResolver implements BeanResolver {

        private final BeanResolver resolver;
        private final Cache<Class<?>, BeanClass> cache = Cache.newL2();

        private CachedBeanResolver(BeanResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public BeanClass resolve(Class<?> beanClass) {
            return cache.getNonNull(beanClass, resolver::resolve);
        }
    }


    private static final class BeanResolverImpl implements BeanResolver {

        private final BeanResolverHandler[] handlers;

        private BeanResolverImpl(List<BeanResolverHandler> handlers) {
            this.handlers = handlers.toArray(new BeanResolverHandler[0]);
        }

        @Override
        public BeanClass resolve(Class<?> beanClass) {
            ContextImpl context = new ContextImpl(beanClass);
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].resolve(beanClass, context);
                if (context.terminate) {
                    return resolveFromContext(beanClass, i + 1, context);
                }
            }
            return resolveFromContext(beanClass, handlers.length, context);
        }

        private BeanClass resolveFromContext(Class<?> beanClass, int times, ContextImpl context) {
            if (context.unsupportedCount == times) {
                throw new UnsupportedOperationException("Cannot resolve this class: " + beanClass);
            }
            return BeanSupport.newBeanClass(beanClass, context.properties);
        }

        private static final class ContextImpl implements BeanResolverHandler.Context {

            private final Class<?> beanClass;
            private final Map<String, BeanProperty> properties = new LinkedHashMap<>();

            private @Nullable @Immutable List<Field> fields;
            private @Nullable @Immutable List<Method> methods;

            private int unsupportedCount = 0;
            private boolean terminate = false;

            private ContextImpl(Class<?> beanClass) {
                this.beanClass = beanClass;
            }

            @Override
            public @Immutable List<Field> fields() {
                if (fields == null) {
                    fields = FieldKit.getFields(beanClass);
                }
                return fields;
            }

            @Override
            public @Immutable List<Method> methods() {
                if (methods == null) {
                    methods = MethodKit.getMethods(beanClass);
                }
                return methods;
            }

            @Override
            public Map<String, BeanProperty> properties() {
                return properties;
            }

            @Override
            public void nonsupport() {
                unsupportedCount++;
            }

            @Override
            public void terminate() {
                terminate = true;
            }
        }
    }
}
