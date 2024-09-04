package xyz.fslabo.common.bean;

import xyz.fslabo.common.cache.Cache;

import java.lang.reflect.Type;

final class BeanProviderImpl implements BeanProvider {

    static BeanProviderImpl DEFAULT_PROVIDER = new BeanProviderImpl(BeanResolver.defaultResolver());

    private final Cache<Type, BeanInfo> cache = Cache.softCache();
    private final BeanResolver resolver;

    BeanProviderImpl(BeanResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public BeanInfo getBeanInfo(Type type) {
        return cache.compute(type, resolver::resolve);
    }
}
