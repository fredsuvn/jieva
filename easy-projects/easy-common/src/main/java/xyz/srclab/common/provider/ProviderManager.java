package xyz.srclab.common.provider;

public interface ProviderManager<T> {

    default void registerProvider(String className) {
        registerProvider(className, false);
    }

    default void registerProvider(T provider) {
        registerProvider(provider, false);
    }

    default void registerProvider(String name, T provider) {
        registerProvider(name, provider, false);
    }

    void registerProvider(String className, boolean isDefault);

    default void registerProvider(T provider, boolean isDefault) {
        registerProvider(provider.getClass().getName(), provider, isDefault);
    }

    void registerProvider(String name, T provider, boolean isDefault);

    T getProvider() throws ProviderNotFoundException;

    T getProvider(String name) throws ProviderNotFoundException;

    void removeProvider(String name);
}
