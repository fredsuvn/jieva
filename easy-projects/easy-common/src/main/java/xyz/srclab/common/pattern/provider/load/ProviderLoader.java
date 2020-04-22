package xyz.srclab.common.pattern.provider.load;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.util.Map;

/**
 * @author sunqian
 */
public interface ProviderLoader<T> {

    static <T> ProviderLoader<T> loadFromClassNames(String classNamesDescriptor) {
        return new ClassNamesProviderLoader<>(classNamesDescriptor);
    }

    T getProvider();

    @Nullable
    T getProvider(String providerName);

    @Immutable
    Map<String, T> getProviders();
}
