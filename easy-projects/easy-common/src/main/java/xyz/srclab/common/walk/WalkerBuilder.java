package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.collection.IterableScheme;
import xyz.srclab.common.collection.MapScheme;
import xyz.srclab.common.design.builder.CachedBuilder;
import xyz.srclab.common.object.UnitPredicate;
import xyz.srclab.common.record.RecordType;
import xyz.srclab.common.record.Recorder;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.Type;
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
                walkHandler.beforeRecord(any, type, this);
                Map<?, ?> map = (Map<?, ?>) any;
                MapScheme mapScheme = MapScheme.getMapScheme(type);
                map.forEach((k, v) -> {
                    walkHandler.doEntry(k, mapScheme.keyType(), v, mapScheme.valueType(), this);
                });
                walkHandler.afterRecord(any, type, this);
            } else if (any instanceof Iterable) {
                walkHandler.beforeList(any, type, this);
                Iterable<?> iterable = (Iterable<?>) any;
                IterableScheme iterableScheme = IterableScheme.getIterableScheme(type);
                Type elementType = iterableScheme.elementType();
                for (@Nullable Object o : iterable) {
                    walk0(o, elementType);
                }
                walkHandler.afterList(any, type, this);
            } else {
                walkHandler.beforeRecord(any, type, this);
                RecordType recordType = recorder.recordType(any);
                recordType.entryMap().forEach((name, entry) -> {
                    walkHandler.doEntry(name, String.class, entry.getValue(any), entry.genericType(), this);
                });
                walkHandler.afterRecord(any, type, this);
            }
        }

        private void walkNull(Type type) {
            if (unitPredicate.test(type)) {
                walkHandler.doUnit(null, type);
                return;
            }
            Class<?> rawType = TypeKit.getRawType(type);
            if (Map.class.isAssignableFrom(rawType)) {
                walkHandler.beforeRecord(null, type, this);
                walkHandler.afterRecord(null, type, this);
            } else if (Iterable.class.isAssignableFrom(rawType)) {
                walkHandler.beforeList(null, type, this);
                walkHandler.afterList(null, type, this);
            } else {
                walkHandler.beforeRecord(null, type, this);
                walkHandler.afterRecord(null, type, this);
            }
        }
    }
}
