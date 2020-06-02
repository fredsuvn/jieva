package xyz.srclab.common;

import org.yaml.snakeyaml.Yaml;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Loader;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.pattern.provider.ProviderLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bootstrap for toova-starter.
 */
public class ToovaBoot {

    private static final String CONFIG_PATH = "META-INF/toova.yaml";

    private static final String version;

    @Immutable
    private static final Map<String, String> defaultProperties;

    @Immutable
    private static final Map<String, String> providerProperties;
    private static final Map<String, Map<String, ?>> providerMap = new ConcurrentHashMap<>();

    static {
        Map<String, Object> toovaProperties = loadYaml(ToovaBoot.class.getClassLoader());
        Map<String, Object> userProperties = loadYaml(Loader.currentClassLoader());
        Map<String, Object> properties = new LinkedHashMap<>();
        mergeProperties(toovaProperties, userProperties, properties);

        version = (String) properties.get("version");
        defaultProperties = MapKit.immutable((Map<String, String>) (properties.get("providers")));
        providerProperties = MapKit.immutable((Map<String, String>) (properties.get("defaults")));
    }

    @Immutable
    private static Map<String, Object> loadYaml(ClassLoader classLoader) {
        @Nullable URL url = classLoader.getResource(CONFIG_PATH);
        if (url == null) {
            return Collections.emptyMap();
        }
        try (InputStream inputStream = url.openStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlProperties = yaml.load(inputStream);
            return MapKit.immutable(yamlProperties);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void mergeProperties(
            Map<String, Object> toovaProperties,
            Map<String, Object> userProperties,
            Map<String, Object> container
    ) {
        toovaProperties.forEach((k, v) -> {
            if (v instanceof Map) {
                if (userProperties.containsKey(k)) {
                    Map<String, Object> newMap = new LinkedHashMap<>();
                    Map<String, Object> users = (Map<String, Object>) userProperties.get(k);
                    mergeProperties((Map<String, Object>) v, users, newMap);
                    container.put(k, newMap);
                } else {
                    container.put(k, v);
                }
            } else {
                container.put(k, userProperties.getOrDefault(k, v));
            }
        });
    }

    public static String getVersion() {
        return version;
    }

    @Immutable
    public static Map<String, String> getProviderProperties() {
        return providerProperties;
    }

    @Immutable
    public static Map<String, String> getDefaultProperties() {
        return defaultProperties;
    }

    public static <T> T getProvider(Class<T> interfaceClass) {
        return getProvider(interfaceClass.getName());
    }

    public static <T> T getProvider(String interfaceName) {
        return MapKit.firstValueNonNull(getProviders(interfaceName));
    }

    public static <T> Map<String, T> getProviders(Class<T> interfaceClass) {
        return getProviders(interfaceClass.getName());
    }

    public static <T> Map<String, T> getProviders(String interfaceName) {
        Map<String, ?> result = providerMap.computeIfAbsent(interfaceName, iName -> {
            @Nullable String providerDescriptor = getProviderProperties().get(iName);
            Checker.checkArguments(
                    providerDescriptor != null, "Cannot find provider for " + interfaceName);
            return ProviderLoader.newStringDescriptorLoader(providerDescriptor).load();
        });
        return (Map<String, T>) result;
    }
}
