package xyz.srclab.common.design.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.collection.MapOps;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
public abstract class AbstractProviderManager<T> implements ProviderManager<T> {

    protected final Map<String, T> providerMaps = new ConcurrentHashMap<>();

    protected @Nullable String defaultProviderName;
    protected @Nullable T defaultProvider;

    @Override
    public synchronized void registerProvider(String className) {
        registerProvider(className, providerMaps.isEmpty());
    }

    @Override
    public synchronized void registerProvider(T provider) {
        registerProvider(provider, providerMaps.isEmpty());
    }

    @Override
    public synchronized void registerProvider(String providerName, T provider, boolean isDefault) {
        providerMaps.put(providerName, provider);
        if (!isDefault) {
            return;
        }
        defaultProviderName = providerName;
        defaultProvider = provider;
    }

    @Override
    public synchronized void setDefaultProvider(String providerName) {
        if (!providerMaps.containsKey(providerName)) {
            throw new NoSuchElementException(providerName);
        }
        T provider = providerMaps.get(providerName);
        defaultProviderName = providerName;
        defaultProvider = provider;
    }

    @Override
    public T getProvider() throws NoSuchElementException {
        @Nullable T provider = defaultProvider;
        if (provider == null) {
            throw new NoSuchElementException();
        }
        return provider;
    }

    @Override
    public @Nullable T getProvider(String providerName) {
        return providerMaps.get(providerName);
    }

    @Override
    public @Immutable Map<String, T> getAllProviders() {
        return MapOps.immutable(providerMaps);
    }

    @Override
    public synchronized void deregisterProvider(String providerName) {
        providerMaps.remove(providerName);
    }
}
