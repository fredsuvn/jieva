package xyz.fslabo.common.bean;

import java.lang.reflect.Type;

/**
 * This provider class provides a standard way to get gek bean info ({@link BeanInfo}).
 *
 * @author sunqian
 */
public interface BeanProvider {

    /**
     * Returns default bean provider.
     *
     * @return default bean provider
     */
    static BeanProvider defaultProvider() {
        return ProviderImpl.DEFAULT_PROVIDER;
    }

    /**
     * Returns a new bean provider with specified resolver.
     *
     * @return specified resolver
     */
    static BeanProvider withResolver(BeanResolver resolver) {
        return new ProviderImpl(resolver);
    }

    /**
     * Returns bean info of given type.
     *
     * @param type given type
     * @return bean info of given type
     */
    BeanInfo getBeanInfo(Type type);
}
