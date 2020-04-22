package xyz.srclab.common.pattern.provider;

import java.util.Map;

/**
 * @author sunqian
 */
public class LoadingProviderManager<T> extends AbstractProviderManager<T> {

    public LoadingProviderManager(String classNamesDescriptor) {
        Map<String, Object> providerMap = ProviderLoader.loadFromClassNames(classNamesDescriptor).getProviders();
        providerMap.forEach((k, v) -> registerProvider(k, (T) v));
        String defaultProviderName = providerMap.keySet().stream().findFirst().orElseThrow(() ->
                new IllegalArgumentException("No default provider found")
        );
        setDefaultProvider(defaultProviderName);
    }
}
