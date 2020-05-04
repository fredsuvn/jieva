package xyz.srclab.common.pattern.provider;

import xyz.srclab.annotation.Immutable;

import java.util.Map;

/**
 * @author sunqian
 */
public interface ProviderLoader<T> {

    static <T> ProviderLoader<T> newStringDescriptorLoader(String stringDescriptor) {
        return new StringDescriptorProviderLoader<>(stringDescriptor);
    }

    @Immutable
    Map<String, T> load();
}
