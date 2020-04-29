package xyz.srclab.common;

import org.yaml.snakeyaml.Yaml;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.pattern.provider.ProviderLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bootstrap for easy-starter.
 */
public class EasyBoot {

    private static final String DEFAULT_CONFIG_PATH = "/META-INF/easy.yaml";
    private static final String[] CUSTOM_CONFIG_PATHS = {
            "/easy.yaml", "/easy.yml", "/META-INF/easy.yaml", "/META-INF/easy.yml"};

    private static final String version;

    @Immutable
    private static final Map<String, String> defaultsProperties;

    @Immutable
    private static final Map<String, String> providerProperties;

    private static final Map<String, ProviderLoader<?>> providerMap = new ConcurrentHashMap<>();

    static {
        Map<String, Object> properties = loadAll();
        version = (String) properties.get("version");
        defaultsProperties = MapHelper.immutable((Map<String, String>) (properties.get("providers")));
        providerProperties = MapHelper.immutable((Map<String, String>) (properties.get("defaults")));
    }

    public static String getVersion() {
        return version;
    }

    @Immutable
    public static Map<String, String> getProviderProperties() {
        return providerProperties;
    }

    @Immutable
    public static Map<String, String> getDefaultsProperties() {
        return defaultsProperties;
    }

    @Immutable
    private static Map<String, Object> loadAll() {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlProperties =
                yaml.load(EasyBoot.class.getResourceAsStream("/META-INF/easy.yaml"));
        return MapHelper.immutable(yamlProperties);
    }

    public static <T> T getProvider(Class<T> interfaceClass) {
        return getProvider(interfaceClass.getName());
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
