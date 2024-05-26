package xyz.fsgek.common.bean;

import xyz.fsgek.common.cache.GekCache;

import java.lang.reflect.Type;

final class ProviderImpl implements GekBeanProvider {

    static ProviderImpl DEFAULT_PROVIDER = new ProviderImpl(GekBeanResolver.defaultResolver());

    private final GekBeanResolver resolver;
    private final GekCache<Type, GekBeanInfo> cache;

    ProviderImpl(GekBeanResolver resolver) {
        this.resolver = resolver;
        this.cache = GekCache.softCache();
    }

    @Override
    public GekBeanInfo getBeanInfo(Type type) {
        if (cache == null) {
            return resolver.resolve(type);
        }
        return cache.get(type, resolver::resolve);
    }
}
