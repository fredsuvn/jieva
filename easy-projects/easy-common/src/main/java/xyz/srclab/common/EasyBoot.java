package xyz.srclab.common;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.util.pattern.provider.ProviderLoader;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bootstrap for easy-starter.
 */
public class EasyBoot {

    @Immutable
    private static final Map<String, String> providerProperties;

    @Immutable
    private static final Map<String, String> defaultsProperties;

    private static final Map<String, ProviderLoader<?>> providerMap = new ConcurrentHashMap<>();

    static {
        providerProperties = loadProviderProperties();
        defaultsProperties = loadDefaultsProperties();
    }

    @Immutable
    public static Map<String, String> getProviderProperties() {
        return providerProperties;
    }

    @Immutable
    private static Map<String, String> loadProviderProperties() {
        return loadProperties("/META-INF/providers.properties");
    }

    @Immutable
    public static Map<String, String> getDefaultsProperties() {
        return defaultsProperties;
    }

    @Immutable
    private static Map<String, String> loadDefaultsProperties() {
        return loadProperties("/META-INF/defaults.properties");
    }

    @Immutable
    private static Map<String, String> loadProperties(String resourcePath) {
        Properties properties = new Properties();
        try {
            properties.load(EasyBoot.class.getResourceAsStream(resourcePath));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        Map<String, String> result = MapHelper.map(
                properties,
                Object::toString,
                Object::toString
        );
        return MapHelper.immutable(result);
    }

    public static <T> T getProvider(String interfaceName) {
        return (T) providerMap.computeIfAbsent(interfaceName, EasyBoot::loadProvider).getProvider();
    }

    @Immutable
    public static <T> Set<T> getProviders(String interfaceName) {
        return (Set<T>) IterableHelper.asSet(
                providerMap.computeIfAbsent(interfaceName, EasyBoot::loadProvider).getProviders().values());
    }

    private static <T> ProviderLoader<T> loadProvider(String interfaceName) {
        String classesDescriptor = getProviderProperties().get(interfaceName);
        Checker.checkArguments(classesDescriptor != null, "No provider of " + interfaceName);
        return ProviderLoader.loadFromClassNames(classesDescriptor);
    }
}
