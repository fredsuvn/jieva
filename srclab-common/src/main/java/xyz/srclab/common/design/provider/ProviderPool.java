package xyz.srclab.common.design.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.MapKit;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public interface ProviderPool<T> {

    static <T> ProviderPool<T> fromProviderMap(Map<String, T> providerMap) {
        return ProviderPoolSupport.newProviderPool(providerMap);
    }

    static <T> ProviderPool<T> fromExpression(String expression) {
        return new ExpressionProviderLoader<T>(expression).load();
    }

    static <T> ProviderPool<T> fromExpression(String expression, ClassLoader classLoader) {
        return new ExpressionProviderLoader<T>(expression, classLoader).load();
    }

    default T defaultProvider() throws NoSuchElementException {
        return MapKit.firstValueNonNull(providerMap());
    }

    @Immutable
    Map<String, T> providerMap();
}
