package xyz.fsgek.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.cache.GekCache;
import xyz.fsgek.common.data.handlers.JavaBeanResolveHandler;

import java.lang.reflect.Type;
import java.util.Collections;

final class Impls {

    static ResolverImpl DEFAULT_RESOLVER = new ResolverImpl(
        Collections.singletonList(JavaBeanResolveHandler.INSTANCE), GekCache.softCache()
    );

    static CopierImpl DEFAULT_COPIER = new CopierImpl();

    static GekDataOption newGekDataOption(GekDataOption.Key key, Object value) {
        return new GekDataOptionImpl(key, value);
    }

    static GekDataResolver newGekDataResolver(
        Iterable<GekDataResolver.Handler> handlers,
        @Nullable GekCache<Type, GekDataDescriptor> cache
    ) {
        return new ResolverImpl(handlers, cache);
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    private static final class GekDataOptionImpl implements GekDataOption {

        private final Key key;
        private final Object value;

        @Override
        public Key getKey() {
            return null;
        }

        @Override
        public Object getValue() {
            return null;
        }
    }
}
