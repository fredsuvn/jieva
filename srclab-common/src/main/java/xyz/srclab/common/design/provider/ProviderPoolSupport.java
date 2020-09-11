package xyz.srclab.common.design.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.MapOps;

import java.util.Map;

/**
 * @author sunqian
 */
final class ProviderPoolSupport {

    static <T> ProviderPool<T> newProviderPool(Map<String, T> providerMap) {
        return new ProviderPoolImp<>(providerMap);
    }

    private static final class ProviderPoolImp<T> implements ProviderPool<T> {

        private final @Immutable Map<String, T> providerMap;

        private ProviderPoolImp(@Immutable Map<String, T> providerMap) {
            this.providerMap = MapOps.immutable(providerMap);
        }

        @Override
        public @Immutable Map<String, T> providerMap() {
            return providerMap;
        }
    }
}
