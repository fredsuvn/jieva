package xyz.srclab.common.util.pattern.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.util.Map;

/**
 * @author sunqian
 */
public interface ProviderLoader<T> {

    static <T> ProviderLoader<T> loadFromClassNames(String classesDescriptor) {
        return new ClassDescriptorProviderLoader<>(classesDescriptor);
    }

    T getProvider();

    @Nullable
    T getProvider(String providerName);

    @Immutable
    Map<String, T> getProviders();
}
