package xyz.srclab.common.record;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;

import java.lang.reflect.Type;

public class RecorderBuilder extends BaseProductCachingBuilder<Recorder> {

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
        Check.checkArgument(resolver != null, "Resolver was not be set");
        return useCache ? new CachedRecorderImpl(this) : new RecorderImpl(this);
    }

    public Recorder build() {
        return buildCaching();
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
        public RecordType resolve(Type type) {
            return resolver.resolve(type);
        }
    }

    private static final class CachedRecorderImpl implements Recorder {

        private final Cache<Type, RecordType> cache = Cache.commonCache();

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
        public RecordType resolve(Type type) {
            return cache.getNonNull(type, resolver::resolve);
        }
    }
}
