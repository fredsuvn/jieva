package xyz.fslabo.common.bean;

import xyz.fslabo.common.cache.Cache;

import java.lang.reflect.Type;

final class BeanProviderImpl implements BeanProvider {

    static BeanProviderImpl DEFAULT_PROVIDER = new BeanProviderImpl(BeanResolver.defaultResolver());

    private final BeanResolver resolver;
    private final Cache<Type, BeanInfo> cache;

    BeanProviderImpl(BeanResolver resolver) {
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
