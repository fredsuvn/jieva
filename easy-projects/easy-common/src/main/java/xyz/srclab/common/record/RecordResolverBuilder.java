package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.design.builder.HandlersBuilder;
import xyz.srclab.common.reflect.FieldKit;
import xyz.srclab.common.reflect.MethodKit;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
        public Map<String, RecordEntry> resolve(Type recordType) {
            ContextImpl context = new ContextImpl(recordType);
            for (int i = 0; i < handlers.length; i++) {
                handlers[i].resolve(recordType, context);
                if (context.terminate) {
                    return resolveFromContext(recordType, i + 1, context);
                }
            }
            return resolveFromContext(recordType, handlers.length, context);
        }

        private Map<String, RecordEntry> resolveFromContext(Type recordType, int times, ContextImpl context) {
            if (context.unsupportCount == times) {
                throw new UnsupportedOperationException("Cannot resolve this class: " + recordType);
            }
            return MapKit.immutable(context.entryMap());
        }

        private static final class ContextImpl implements RecordResolverHandler.Context {

            private final Type recordType;

            private @Nullable Map<String, RecordEntry> entryMap;
            private @Nullable @Immutable List<Field> fields;
            private @Nullable @Immutable List<Method> methods;

            private int unsupportCount = 0;
            private boolean terminate = false;

            private ContextImpl(Type recordType) {
                this.recordType = recordType;
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
                    fields = FieldKit.getFields(TypeKit.getRawType(recordType));
                }
                return fields;
            }

            @Override
            public @Immutable List<Method> methods() {
                if (methods == null) {
                    methods = MethodKit.getMethods(TypeKit.getRawType(recordType));
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
