package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.design.builder.CachedBuilder;

import java.lang.reflect.Type;
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
        Check.checkArguments(resolver != null, "Resolver was not be set");
        return useCache ? new CachedRecorderImpl(this) : new RecorderImpl(this);
    }

    private static final class RecorderImpl implements Recorder {

        private final RecordResolver resolver;

        private RecorderImpl(RecorderBuilder builder) {
            assert builder.resolver != null;
            this.resolver = builder.resolver;
        }

        @Override
        public RecordResolver resolver() {
            return resolver;
        }

        @Override
        public Map<String, RecordEntry> resolve(Type recordType) {
            return resolver.resolve(recordType);
        }
    }

    private static final class CachedRecorderImpl implements Recorder {

        private final Cache<Type, Map<String, RecordEntry>> cache = Cache.newCommonCache();

        private final RecordResolver resolver;

        private CachedRecorderImpl(RecorderBuilder builder) {
            assert builder.resolver != null;
            this.resolver = builder.resolver;
        }

        @Override
        public RecordResolver resolver() {
            return resolver;
        }

        @Override
        public @Immutable Map<String, RecordEntry> resolve(Type recordType) {
            return cache.getNonNull(recordType, resolver::resolve);
        }
    }
}
