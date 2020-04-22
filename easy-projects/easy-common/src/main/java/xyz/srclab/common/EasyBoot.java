package xyz.srclab.common;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.map.MapHelper;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class EasyBoot {

    @Immutable
    private static final Map<String, String> providerProperties;

    static {
        providerProperties = loadProviderProperties();
    }

    @Immutable
    public static Map<String, String> getProviderProperties() {
        return providerProperties;
    }

    @Immutable
    private static Map<String, String> loadProviderProperties() {
        Properties properties = new Properties();
        try {
            properties.load(EasyBoot.class.getResourceAsStream("/META-INF/providers.properties"));
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
