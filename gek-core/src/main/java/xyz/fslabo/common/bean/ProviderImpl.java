package xyz.fslabo.common.bean;

import xyz.fslabo.common.cache.Cache;

import java.lang.reflect.Type;

final class ProviderImpl implements BeanProvider {

    static ProviderImpl DEFAULT_PROVIDER = new ProviderImpl(BeanResolver.defaultResolver());

    private final BeanResolver resolver;
    private final Cache<Type, BeanInfo> cache;

    ProviderImpl(BeanResolver resolver) {
        this.resolver = resolver;
        this.cache = Cache.softCache();
    }

    @Override
    public BeanInfo getBeanInfo(Type type) {
        if (cache == null) {
            return resolver.resolve(type);
        }
        return cache.compute(type, resolver::resolve);
    }
}
