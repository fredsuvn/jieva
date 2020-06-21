package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.pattern.builder.CachedBuilder;

import java.util.Map;

public class RecorderBuilder extends CachedBuilder<Recorder> {

    static RecorderBuilder newBuilder() {
        return new RecorderBuilder();
    }

    private @Nullable RecordResolver resolver;
    private boolean useCache = true;

    private RecorderBuilder() {
    }

    public RecorderBuilder resolver(RecordResolver resolver) {
        this.resolver = resolver;
        updateState();
        return this;
    }

    public RecorderBuilder useCache(boolean useCache) {
        this.useCache = useCache;
        updateState();
        return this;
    }

    @Override
    protected Recorder buildNew() {
        return useCache ? new CachedRecorderImpl(this) : new RecorderImpl(this);
    }

    private static final class RecorderImpl implements Recorder {

        private final RecordResolver resolver;

        private RecorderImpl(RecorderBuilder builder) {
            this.resolver = builder.resolver == null ? RecordResolver.defaultResolver() : builder.resolver;
        }

        @Override
        public RecordResolver resolver() {
            return resolver;
        }

        @Override
        public Map<String, RecordEntry> resolve(Class<?> recordClass) {
            return resolver.resolve(recordClass);
        }
    }

    private static final class CachedRecorderImpl implements Recorder {

        private final Cache<Class<?>, Map<String, RecordEntry>> cache = Cache.newL2();

        private final RecordResolver resolver;

        private CachedRecorderImpl(RecorderBuilder builder) {
            this.resolver = builder.resolver == null ? RecordResolver.defaultResolver() : builder.resolver;
        }

        @Override
        public RecordResolver resolver() {
            return resolver;
        }

        @Override
        public @Immutable Map<String, RecordEntry> resolve(Class<?> recordClass) {
            return cache.getNonNull(recordClass, resolver::resolve);
        }
    }
}
