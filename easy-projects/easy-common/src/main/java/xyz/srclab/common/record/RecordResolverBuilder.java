package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.design.builder.HandlersBuilder;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.reflect.MethodKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class RecordResolverBuilder extends
        HandlersBuilder<RecordResolver, RecordResolverHandler, RecordResolverBuilder> {

    static RecordResolverBuilder newBuilder() {
        return new RecordResolverBuilder();
    }

    private RecordResolverBuilder() {
    }

    @Override
    protected RecordResolver buildNew() {
        Check.checkState(!handlers.isEmpty(), "There is no handler");
        return new RecorderImpl(handlers);
    }

    private static final class RecorderImpl implements RecordResolver {

        private final RecordResolverHandler[] handlers;

        private RecorderImpl(List<RecordResolverHandler> handlers) {
            this.handlers = ArrayKit.toArray(handlers, RecordResolverHandler.class);
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
            if (context.unsupportCount == times) {
                throw new UnsupportedOperationException("Cannot resolve this class: " + recordClass);
            }
            return MapKit.immutable(context.entryMap());
        }

        private static final class ContextImpl implements RecordResolverHandler.Context {

            private final Class<?> recordClass;

            private @Nullable Map<String, RecordEntry> entryMap;
            private @Nullable @Immutable List<Field> fields;
            private @Nullable @Immutable List<Method> methods;

            private int unsupportCount = 0;
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
                unsupportCount++;
            }

            @Override
            public void terminate() {
                terminate = true;
            }
        }
    }
}
