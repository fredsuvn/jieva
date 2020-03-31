package xyz.srclab.common.provider;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public abstract class AbstractProviderManager<T> implements ProviderManager<T> {

    private final Map<String, T> providerMap = new ConcurrentHashMap<>();
    private @Nullable String defaultProviderName;
    private @Nullable T defaultProvider;

    @Override
    public void registerProvider(String className, boolean isDefault) {
        synchronized (this) {
            if (providerMap.containsKey(className)) {
                throw new IllegalStateException("Provider has already registered: " + className);
            }
            @Nullable Class<?> providerClass = EnvironmentHelper.findClass(className);
            if (providerClass == null) {
                throw new IllegalArgumentException(new ClassNotFoundException(className));
            }
            T provider = createProvider((Class<T>) providerClass);
            providerMap.put(className, provider);
            if (isDefault) {
                defaultProviderName = className;
                defaultProvider = provider;
            }
        }
    }

    @Override
    public void registerProvider(String name, T provider, boolean isDefault) {
        synchronized (this) {
            if (providerMap.containsKey(name)) {
                throw new IllegalStateException("Provider has already registered: " + name);
            }
            providerMap.put(name, provider);
            if (isDefault) {
                defaultProviderName = name;
                defaultProvider = provider;
            }
        }
    }

    @Override
    public T getProvider() {
        if (defaultProvider != null) {
            return defaultProvider;
        }
        synchronized (this) {
            if (defaultProvider == null) {
                defaultProvider = createDefaultProvider();
                providerMap.put(defaultProvider.getClass().getName(), defaultProvider);
            }
            return defaultProvider;
        }
    }

    @Override
    public T getProvider(String name) {
        T provider = providerMap.get(name);
        if (provider == null) {
            throw new ProviderNotFoundException(name);
        }
        return provider;
    }

    @Override
    public void removeProvider(String name) {
        synchronized (this) {
            providerMap.remove(name);
            if (defaultProviderName != null && defaultProviderName.equals(name)) {
                defaultProviderName = null;
                defaultProvider = null;
            }
        }
    }

    protected T createProvider(Class<T> providerClass) {
        return InstanceHelper.newInstance(providerClass);
    }

    protected abstract T createDefaultProvider();
}
