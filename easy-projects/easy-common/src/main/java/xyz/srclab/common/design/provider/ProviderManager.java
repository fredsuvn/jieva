package xyz.srclab.common.design.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.base.Loader;
import xyz.srclab.common.reflect.ClassKit;

import java.util.Map;
import java.util.NoSuchElementException;

public interface ProviderManager<T> {

    void registerProvider(String className);

    void registerProvider(T provider);

    default void registerProvider(String className, boolean isDefault) {
        @Nullable Class<T> providerClass = Loader.loadClass(className);
        Check.checkArguments(providerClass != null, "Can not find class: " + className);
        T provider = ClassKit.newInstance(providerClass);
        registerProvider(className, provider, isDefault);
    }

    default void registerProvider(T provider, boolean isDefault) {
        registerProvider(provider.getClass().getName(), provider, isDefault);
    }

    default void registerProvider(String providerName, T provider) {
        registerProvider(providerName, provider, false);
    }

    void registerProvider(String providerName, T provider, boolean isDefault);

    void setDefaultProvider(String providerName) throws NoSuchElementException;

    T getProvider() throws NoSuchElementException;

    @Nullable
    T getProvider(String providerName);

    @Immutable
    Map<String, T> getAllProviders();

    void deregisterProvider(String providerName);
}
