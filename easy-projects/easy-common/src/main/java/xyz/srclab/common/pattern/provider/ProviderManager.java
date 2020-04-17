package xyz.srclab.common.pattern.provider;

import java.sql.DriverManager;
import java.util.function.Supplier;

public interface ProviderManager<T> {

    default void registerProvider(String className) {
        registerProvider(className, false);
    }

    default void registerProvider(T provider) {
        registerProvider(provider, false);
    }

    default void registerProvider(String providerName, T provider) {
        registerProvider(providerName, false, provider);
    }

    default void registerProvider(String providerName, Supplier<T> providerSupplier) {
        registerProvider(providerName, false, providerSupplier);
    }

    void registerProvider(String className, boolean isDefault);

    default void registerProvider(T provider, boolean isDefault) {
        registerProvider(provider.getClass().getName());
    }

    void registerProvider(String providerName, boolean isDefault, T provider);

    void registerProvider(String providerName, boolean isDefault, Supplier<T> providerSupplier);

    T getProvider() throws ProviderNotFoundException;

    T getProvider(String providerName) throws ProviderNotFoundException;

    void removeProvider(String providerName);
}
