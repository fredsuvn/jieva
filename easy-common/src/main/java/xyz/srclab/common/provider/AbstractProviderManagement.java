package xyz.srclab.common.provider;

import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.common.reflect.ReflectHelper;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public abstract class AbstractProviderManagement<T> implements ProviderManagement<T> {

    private final Map<String, T> providerMap = new ConcurrentHashMap<>();
    private T defaultProvider;

    @Override
    public void registerProvider(String className, boolean isDefault) {
        synchronized (this) {
            if (providerMap.containsKey(className)) {
                throw new IllegalStateException("Provider has already registered: " + className);
            }
            Class<?> providerClass = EnvironmentHelper.findClass(className);
            if (providerClass == null) {
                throw new IllegalArgumentException(new ClassNotFoundException(className));
            }
            T provider = createProvider((Class<T>) providerClass);
            providerMap.put(className, provider);
            if (isDefault) {
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
    public T getProvider(String className) {
        T provider = providerMap.get(className);
        if (provider == null) {
            throw new IllegalArgumentException("Provider not found: " + className);
        }
        return provider;
    }

    @Override
    public void removeProvider(String className) {
        synchronized (this) {
            providerMap.remove(className);
            if (defaultProvider != null && defaultProvider.getClass().getName().equals(className)) {
                defaultProvider = null;
            }
        }
    }

    protected T createProvider(Class<T> providerClass) {
        return ReflectHelper.newInstance(providerClass);
    }

    protected abstract T createDefaultProvider();
}
