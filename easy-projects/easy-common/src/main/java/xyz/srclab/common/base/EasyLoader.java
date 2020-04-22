package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.provider.load.ProviderLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunqian
 */
public class EasyLoader {

    private static final Map<String, Map<String, Object>> globalProviderMap = new ConcurrentHashMap<>();

    static {
        loadAll();
    }

    public static <T> T getProvider(String interfaceName) {
        @Nullable Object result = globalProviderMap.get(interfaceName);
        Checker.checkState(result != null, "No implementation found: " + interfaceName);
        return (T) result;
    }

    private static void loadAll() {
        loadProviders();
    }

    private static void loadProviders() {
        InputStream inputStream = EasyLoader.class.getResourceAsStream("/META-INF/providers.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        Set<String> interfaceNames = properties.stringPropertyNames();
        for (String interfaceName : interfaceNames) {
            String implementation = properties.getProperty(interfaceName);
            Map<String, Object> providerMap = ProviderLoader.loadFromClassNames(implementation).getProviders();
            globalProviderMap.put(interfaceName, providerMap);
        }
    }
}
