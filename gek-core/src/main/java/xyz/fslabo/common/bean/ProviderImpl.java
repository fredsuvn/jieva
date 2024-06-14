package xyz.fslabo.common.bean;

import xyz.fslabo.common.cache.GekCache;

import java.lang.reflect.Type;

final class ProviderImpl implements BeanProvider {

    static ProviderImpl DEFAULT_PROVIDER = new ProviderImpl(BeanResolver.defaultResolver());

    private final BeanResolver resolver;
    private final GekCache<Type, BeanInfo> cache;

    ProviderImpl(BeanResolver resolver) {
        this.resolver = resolver;
        this.cache = GekCache.softCache();
    }

    @Override
    public BeanInfo getBeanInfo(Type type) {
        if (cache == null) {
            return resolver.resolve(type);
        }
        return cache.get(type, resolver::resolve);
    }
}
