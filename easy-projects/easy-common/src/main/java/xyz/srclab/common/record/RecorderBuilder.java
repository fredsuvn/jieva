package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.collection.CollectionKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.reflect.MethodKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class RecorderBuilder extends CachedBuilder<Recorder> {

    static RecorderBuilder newBuilder() {
        return new RecorderBuilder();
    }

    private final List<ResolveHandler> resolveHandlers = new LinkedList<>();
    private boolean useCache = true;

    private RecorderBuilder() {
    }

    public RecorderBuilder resolveHandlers(ResolveHandler... resolveHandlers) {
        return resolveHandlers(Arrays.asList(resolveHandlers));
    }

    public RecorderBuilder resolveHandlers(Iterable<ResolveHandler> resolveHandlers) {
        CollectionKit.addAll(this.resolveHandlers, resolveHandlers);
        this.updateState();
        return this;
    }

    public RecorderBuilder useCache(boolean useCache) {
        this.useCache = useCache;
        this.updateState();
        return this;
    }

    @Override
    protected Recorder buildNew() {
        if (resolveHandlers.isEmpty()) {
            throw new IllegalArgumentException("There is no handler added");
        }
        Recorder recorder = new RecorderImpl(resolveHandlers);
        return useCache ? new CachedRecorder(recorder) : recorder;
    }

    private static final class CachedRecorder implements Recorder {

        private final Recorder recorder;
        private final Cache<Class<?>, Map<String, RecordEntry>> cache = Cache.newL2();

        private CachedRecorder(Recorder recorder) {
            this.recorder = recorder;
        }

        @Override
        public Map<String, RecordEntry> resolve(Class<?> beanClass) {
            return cache.getNonNull(beanClass, recorder::resolve);
        }
    }

    private static final class RecorderImpl implements Recorder {

        private final ResolveHandler[] handlers;

        private RecorderImpl(List<ResolveHandler> handlers) {
            this.handlers = ArrayKit.toArray(handlers, ResolveHandler.class);
        }

        @Override
        public Map<String, RecordEntry> resolve(Class<?> beanClass) {
            ContextImpl context = new ContextImpl(beanClass);
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].resolve(beanClass, context);
                if (context.terminate) {
                    return resolveFromContext(beanClass, i + 1, context);
                }
            }
            return resolveFromContext(beanClass, handlers.length, context);
        }

        private Map<String, RecordEntry> resolveFromContext(Class<?> recordClass, int times, ContextImpl context) {
            if (context.unsupportedCount == times) {
                throw new UnsupportedOperationException("Cannot resolve this class: " + recordClass);
            }
            return MapKit.immutable(context.entryMap());
        }

        private static final class ContextImpl implements ResolveHandler.Context {

            private final Class<?> recordClass;

            private @Nullable Map<String, RecordEntry> entryMap;
            private @Nullable @Immutable List<Field> fields;
            private @Nullable @Immutable List<Method> methods;

            private int unsupportedCount = 0;
            private boolean terminate = false;

            private ContextImpl(Class<?> recordClass) {
                this.recordClass = recordClass;
            }

            @Override
            public Map<String, RecordEntry> entryMap() {
                if (entryMap == null) {
                    entryMap = new LinkedHashMap<>();
                }
                return entryMap;
            }

            @Override
            public @Immutable List<Field> fields() {
                if (fields == null) {
                    fields = FieldKit.getFields(recordClass);
                }
                return fields;
            }

            @Override
            public @Immutable List<Method> methods() {
                if (methods == null) {
                    methods = MethodKit.getMethods(recordClass);
                }
                return methods;
            }

            @Override
            public void unsupport() {
                unsupportedCount++;
            }

            @Override
            public void terminate() {
                terminate = true;
            }
        }
    }
}
