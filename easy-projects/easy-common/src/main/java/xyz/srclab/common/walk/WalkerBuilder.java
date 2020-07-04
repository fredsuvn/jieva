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

    private @Nullable WalkHandler walkHandler;
    private @Nullable UnitPredicate unitPredicate;
    private @Nullable Recorder recorder;

    public WalkerBuilder walkHandler(WalkHandler walkHandler) {
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
        Check.checkArguments(unitPredicate != null, "Need unit predicate");
        Check.checkArguments(recorder != null, "Need walk recorder");
        return new WalkerImpl(walkHandler, unitPredicate, recorder);
    }

    private static final class WalkerImpl implements Walker {

        private final WalkHandler walkHandler;
        private final UnitPredicate unitPredicate;
        private final Recorder recorder;

        private WalkerImpl(WalkHandler walkHandler, UnitPredicate unitPredicate, Recorder recorder) {
            this.walkHandler = walkHandler;
            this.unitPredicate = unitPredicate;
            this.recorder = recorder;
        }

        @Override
        public void walk(Object any, Type type) {
            walk0(any, type);
        }

        private void walk0(@Nullable Object any, Type type) {
            if (any == null) {
                walkNull(type);
                return;
            }
            if (unitPredicate.test(type)) {
                walkHandler.doUnit(any, type);
            } else if (any instanceof Map) {
                walkHandler.beforeObject(any, type);
                Map<?, ?> map = (Map<?, ?>) any;
                MapScheme mapScheme = MapScheme.getMapScheme(type);
                map.forEach((k, v) -> {
                    walkHandler.doElement(k, mapScheme.keyType(), v, mapScheme.valueType());
                    walk0(v, mapScheme.valueType());
                });
                walkHandler.afterObject(any, type);
            } else if (any instanceof Iterable) {
                walkHandler.beforeList(any, type);
                Iterable<?> iterable = (Iterable<?>) any;
                IterableScheme iterableScheme = IterableScheme.getIterableScheme(type);
                Type elementType = iterableScheme.elementType();
                int index = 0;
                for (@Nullable Object o : iterable) {
                    walkHandler.doElement(index, int.class, o, elementType);
                    walk0(o, elementType);
                }
                walkHandler.afterList(any, type);
            } else if (ArrayKit.isArray(type)) {
                walkHandler.beforeList(any, type);
                List<?> list = ArrayKit.asList(any);
                Type componentType = ArrayKit.getComponentType(type);
                int index = 0;
                for (@Nullable Object o : list) {
                    walkHandler.doElement(index, int.class, o, componentType);
                    walk0(o, componentType);
                }
                walkHandler.afterList(any, type);
            } else {
                walkHandler.beforeObject(any, type);
                RecordType recordType = recorder.recordType(any);
                recordType.entryMap().forEach((name, entry) -> {
                    walkHandler.doElement(name, String.class, entry.getValue(any), entry.genericType());
                });
                walkHandler.afterObject(any, type);
            }
        }

        private void walkNull(Type type) {
            if (unitPredicate.test(type)) {
                walkHandler.doUnit(null, type);
                return;
            }
            Class<?> rawType = TypeKit.getRawType(type);
            if (Map.class.isAssignableFrom(rawType)) {
                walkHandler.beforeObject(null, type);
                walkHandler.afterObject(null, type);
            } else if (Iterable.class.isAssignableFrom(rawType) || ArrayKit.isArray(type)) {
                walkHandler.beforeList(null, type);
                walkHandler.afterList(null, type);
            } else {
                walkHandler.beforeObject(null, type);
                walkHandler.afterObject(null, type);
            }
        }
    }
}
