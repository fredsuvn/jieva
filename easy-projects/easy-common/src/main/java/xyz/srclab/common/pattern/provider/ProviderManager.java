package xyz.srclab.common.pattern.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Context;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import java.util.Map;
import java.util.NoSuchElementException;

public interface ProviderManager<T> {

    void registerProvider(String className);

    void registerProvider(T provider);

    default void registerProvider(String className, boolean isDefault) {
        @Nullable Class<T> providerClass = Context.getClass(className);
        Checker.checkArguments(providerClass != null, "Can not find class: " + className);
        T provider = InstanceHelper.newInstance(providerClass);
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
