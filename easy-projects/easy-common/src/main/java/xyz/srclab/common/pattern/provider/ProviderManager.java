package xyz.srclab.common.pattern.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.environment.ClassPathHelper;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import java.util.Map;
import java.util.NoSuchElementException;

public interface ProviderManager<T> {

    default void registerProvider(String className) {
        registerProvider(className, false);
    }

    default void registerProvider(T provider) {
        registerProvider(provider, false);
    }

    default void registerProvider(String className, boolean isDefault) {
        @Nullable Class<T> providerClass = ClassPathHelper.getClass(className);
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

    void setDefaultProvider(String providerName);

    T getProvider() throws NoSuchElementException;

    @Nullable
    T getProvider(String providerName);

    @Immutable
    Map<String, T> getAllProviders();

    void removeProvider(String providerName);
}
