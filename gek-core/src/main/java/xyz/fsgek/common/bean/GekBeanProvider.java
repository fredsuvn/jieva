package xyz.fsgek.common.bean;

import java.lang.reflect.Type;

/**
 * This provider class provides a standard way to get gek bean info ({@link GekBeanInfo}).
 *
 * @author sunqian
 */
public interface GekBeanProvider {

    /**
     * Returns default bean provider.
     *
     * @return default bean provider
     */
    static GekBeanProvider defaultProvider() {
        return ProviderImpl.DEFAULT_PROVIDER;
    }

    /**
     * Returns a new bean provider with specified resolver.
     *
     * @return specified resolver
     */
    static GekBeanProvider withResolver(GekBeanResolver resolver) {
        return new ProviderImpl(resolver);
    }

    /**
     * Returns bean info of given type.
     *
     * @param type given type
     * @return bean info of given type
     */
    GekBeanInfo getBeanInfo(Type type);
}
