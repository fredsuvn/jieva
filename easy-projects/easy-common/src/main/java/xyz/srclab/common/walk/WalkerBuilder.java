package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.collection.IterableScheme;
import xyz.srclab.common.collection.MapScheme;
import xyz.srclab.common.design.builder.CachedBuilder;
import xyz.srclab.common.object.UnitPredicate;
import xyz.srclab.common.record.RecordType;
import xyz.srclab.common.record.Recorder;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class WalkerBuilder extends CachedBuilder<Walker> {

    public static WalkerBuilder newBuilder() {
        return new WalkerBuilder();
    }

    private @Nullable WalkHandler<?> walkHandler;
    private @Nullable UnitPredicate unitPredicate;
    private @Nullable Recorder recorder;

    public WalkerBuilder walkHandler(WalkHandler<?> walkHandler) {
        this.walkHandler = walkHandler;
        return this;
    }

    public WalkerBuilder unitPredicate(UnitPredicate unitPredicate) {
        this.unitPredicate = unitPredicate;
        return this;
    }

    public WalkerBuilder recorder(Recorder recorder) {
        this.recorder = recorder;
        return this;
    }

    @Override
    protected Walker buildNew() {
        Check.checkArguments(walkHandler != null, "Need walk handler");
        if (unitPredicate == null) {
            unitPredicate = UnitPredicate.defaultPredicate();
        }
        if (recorder == null) {
            recorder = Recorder.defaultRecorder();
        }
        return new WalkerImpl<>(walkHandler, unitPredicate, recorder);
    }

    private static final class WalkerImpl<S> implements Walker {

        private final WalkHandler<S> walkHandler;
        private final UnitPredicate unitPredicate;
        private final Recorder recorder;

        private WalkerImpl(WalkHandler<S> walkHandler, UnitPredicate unitPredicate, Recorder recorder) {
            this.walkHandler = walkHandler;
            this.unitPredicate = unitPredicate;
            this.recorder = recorder;
        }

        @Override
        public void walk(@Nullable Object any, Type type) {
            S stack = walkHandler.newStack();
            walk0(any, type, stack);
        }

        public void walk0(@Nullable Object any, Type type, S stack) {
            if (any == null) {
                walkNull(type, stack);
                return;
            }
            if (unitPredicate.test(type)) {
                walkHandler.doUnit(any, type, stack);
            } else if (any instanceof Map) {
                walkHandler.beforeObject(any, type, stack);
                Map<?, ?> map = (Map<?, ?>) any;
                MapScheme mapScheme = MapScheme.getMapScheme(type);
                map.forEach((k, v) -> {
                    walkHandler.doObjectElement(
                            k, mapScheme.keyType(), v, mapScheme.valueType(), stack, this);
                });
                walkHandler.afterObject(any, type, stack);
            } else if (any instanceof Iterable) {
                walkHandler.beforeList(any, type, stack);
                Iterable<?> iterable = (Iterable<?>) any;
                IterableScheme iterableScheme = IterableScheme.getIterableScheme(type);
                Type elementType = iterableScheme.elementType();
                int index = 0;
                for (@Nullable Object o : iterable) {
                    walkHandler.doListElement(index, o, elementType, stack, this);
                }
                walkHandler.afterList(any, type, stack);
            } else if (ArrayKit.isArray(type)) {
                walkHandler.beforeList(any, type, stack);
                List<?> list = ArrayKit.asList(any);
                Type componentType = ArrayKit.getComponentType(type);
                int index = 0;
                for (@Nullable Object o : list) {
                    walkHandler.doListElement(index, o, componentType, stack, this);
                }
                walkHandler.afterList(any, type, stack);
            } else {
                walkHandler.beforeObject(any, type, stack);
                RecordType recordType = recorder.recordType(any);
                recordType.entryMap().forEach((name, entry) -> {
                    walkHandler.doObjectElement(
                            name, String.class, entry.getValue(any), entry.genericType(), stack, this);
                });
                walkHandler.afterObject(any, type, stack);
            }
        }

        private void walkNull(Type type, S stack) {
            if (unitPredicate.test(type)) {
                walkHandler.doUnit(null, type, stack);
                return;
            }
            Class<?> rawType = TypeKit.getRawType(type);
            if (Map.class.isAssignableFrom(rawType)) {
                walkHandler.beforeObject(null, type, stack);
                walkHandler.afterObject(null, type, stack);
            } else if (Iterable.class.isAssignableFrom(rawType) || ArrayKit.isArray(type)) {
                walkHandler.beforeList(null, type, stack);
                walkHandler.afterList(null, type, stack);
            } else {
                walkHandler.beforeObject(null, type, stack);
                walkHandler.afterObject(null, type, stack);
            }
        }
    }
}
