package xyz.srclab.common.provider;

public interface ProviderManagement<T> {

    default void registerProvider(String className) {
        registerProvider(className, false);
    }

    void registerProvider(String className, boolean isDefault);

    T getProvider();

    T getProvider(String className);

    void removeProvider(String className);
}
