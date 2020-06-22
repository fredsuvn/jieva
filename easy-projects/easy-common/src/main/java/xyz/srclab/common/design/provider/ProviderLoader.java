package xyz.srclab.common.design.provider;

import xyz.srclab.annotation.Immutable;

import java.util.Map;

/**
 * @author sunqian
 */
public interface ProviderLoader<T> {

    static <T> ProviderLoader<T> newStringDescriptorLoader(String stringDescriptor) {
        return new StringDescriptorProviderLoader<>(stringDescriptor);
    }

    static <T> ProviderLoader<T> newStringDescriptorLoader(String stringDescriptor, ClassLoader classLoader) {
        return new StringDescriptorProviderLoader<>(stringDescriptor, classLoader);
    }

    @Immutable
    Map<String, T> load();
}
