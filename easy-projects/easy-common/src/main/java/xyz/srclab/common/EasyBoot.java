package xyz.srclab.common;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.map.MapHelper;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class EasyBoot {

    @Immutable
    private static final Map<String, String> providerProperties;

    @Immutable
    private static final Map<String, String> defaultsProperties;

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
}
