package xyz.srclab.common.pattern.provider;

import xyz.srclab.common.pattern.provider.load.ProviderLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author sunqian
 */
public class LoadingProviderManager<T> extends AbstractProviderManager<T> {

    public LoadingProviderManager() {

    }

    public LoadingProviderManager(URL url, String interfaceName) {
        String toString = url.toString();
        if (toString.endsWith("properties")) {
            Map<String, T> providerMap =
        }
    }

    private void loadFromProperties(URL url) {
        Properties properties = new Properties();
        try {
            properties.load(url.);
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
