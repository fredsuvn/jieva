package xyz.srclab.common.pattern.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.map.MapHelper;

import java.util.Map;

/**
 * @author sunqian
 */
public abstract class AbstractProviderLoader<T> implements ProviderLoader<T> {

    protected final Map<String, T> providerMap;
    protected final T first;

    AbstractProviderLoader(Map<String, T> providerMap) {
        Checker.checkState(!providerMap.isEmpty(), "No provider found");
        this.providerMap = MapHelper.immutable(providerMap);
        this.first = this.providerMap.values().stream().findFirst().orElseThrow(
                () -> new IllegalStateException("No provider found"));
    }

    @Override
    public T getProvider() {
        return first;
    }

    @Override
    public T getProvider(String providerName) {
        return providerMap.get(providerName);
    }

    @Override
    public @Immutable Map<String, T> getProviders() {
        return providerMap;
    }
}
