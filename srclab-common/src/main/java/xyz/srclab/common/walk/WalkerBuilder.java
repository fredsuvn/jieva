package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.ArrayKit;
import xyz.srclab.common.collection.IterableScheme;
import xyz.srclab.common.collection.MapScheme;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;
import xyz.srclab.common.lang.stack.Stack;
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
public class WalkerBuilder<C> extends BaseProductCachingBuilder<Walker<C>> {

    public static <C> WalkerBuilder<C> newBuilder(WalkHandler<C> walkHandler) {
        return new WalkerBuilder<>(walkHandler);
    }

    private final WalkHandler<C> walkHandler;
    private @Nullable UnitPredicate unitPredicate;
    private @Nullable Recorder recorder;

    public WalkerBuilder(WalkHandler<C> walkHandler) {
        this.walkHandler = walkHandler;
    }

    public WalkerBuilder<C> unitPredicate(UnitPredicate unitPredicate) {
        this.unitPredicate = unitPredicate;
        return this;
    }

    public WalkerBuilder<C> recorder(Recorder recorder) {
        this.recorder = recorder;
        return this;
    }

    @Override
    protected Walker<C> buildNew() {
        if (unitPredicate == null) {
            unitPredicate = UnitPredicate.defaultPredicate();
        }
        if (recorder == null) {
            recorder = Recorder.defaultRecorder();
        }
        return new WalkerImpl<>(walkHandler, unitPredicate, recorder);
    }

    public Walker<C> build() {
        return super.buildCaching();
    }

    private static final class WalkerImpl<C> implements Walker<C> {

        private final WalkHandler<C> walkHandler;
        private final UnitPredicate unitPredicate;
        private final Recorder recorder;

        private WalkerImpl(WalkHandler<C> walkHandler, UnitPredicate unitPredicate, Recorder recorder) {
            this.walkHandler = walkHandler;
            this.unitPredicate = unitPredicate;
            this.recorder = recorder;
        }

        @Override
        public C walk(@Nullable Object any, Type type) {
            C context = walkHandler.newContext();
            Stack<C> contextStack = Stack.newStack();
            contextStack.push(context);
            return walk0(any, type, contextStack);
        }

        public C walk0(@Nullable Object any, Type type, Stack<C> contextStack) {
            if (any == null) {
                walkNull(type, contextStack);
                return contextStack.popNonNull();
            }
            if (unitPredicate.test(type)) {
                walkHandler.doUnit(any, type, contextStack);
            } else if (any instanceof Map) {
                walkHandler.beforeObject(any, type, contextStack);
                Map<?, ?> map = (Map<?, ?>) any;
                MapScheme mapScheme = MapScheme.getMapScheme(type);
                C nextContext = walkHandler.newContext(contextStack.topNonNull());
                contextStack.push(nextContext);
                map.forEach((k, v) -> {
                    walkHandler.doObjectElement(
                            k, mapScheme.keyType(), v, mapScheme.valueType(), contextStack,
                            (o, t) -> walk0(o, t, contextStack));
                });
                contextStack.pop();
                walkHandler.afterObject(any, type, contextStack);
            } else if (any instanceof Iterable) {
                walkHandler.beforeList(any, type, contextStack);
                Iterable<?> iterable = (Iterable<?>) any;
                IterableScheme iterableScheme = IterableScheme.getIterableScheme(type);
                Type elementType = iterableScheme.elementType();
                walkIterable(any, type, contextStack, iterable, elementType);
            } else if (ArrayKit.isArray(type)) {
                walkHandler.beforeList(any, type, contextStack);
                List<?> list = ArrayKit.asList(any);
                Type componentType = ArrayKit.getComponentType(type);
                walkIterable(any, type, contextStack, list, componentType);
            } else {
                walkHandler.beforeObject(any, type, contextStack);
                RecordType recordType = recorder.recordType(any);
                C nextContext = walkHandler.newContext(contextStack.topNonNull());
                contextStack.push(nextContext);
                recordType.entryMap().forEach((name, entry) -> {
                    walkHandler.doObjectElement(
                            name, String.class, entry.getValue(any), entry.genericType(), contextStack,
                            (o, t) -> walk0(o, t, contextStack));
                });
                contextStack.pop();
                walkHandler.afterObject(any, type, contextStack);
            }
            return contextStack.popNonNull();
        }

        public void walkIterable(
                @Nullable Object any, Type type, Stack<C> contextStack, Iterable<?> iterable, Type elementType) {
            C nextStack = walkHandler.newContext(contextStack.topNonNull());
            contextStack.push(nextStack);
            int index = 0;
            for (@Nullable Object e : iterable) {
                walkHandler.doListElement(index, e, elementType, contextStack,
                        (o, t) -> walk0(o, t, contextStack));
            }
            contextStack.pop();
            walkHandler.afterList(any, type, contextStack);
        }

        private void walkNull(Type type, Stack<C> contextStack) {
            if (unitPredicate.test(type)) {
                walkHandler.doUnit(null, type, contextStack);
                return;
            }
            Class<?> rawType = TypeKit.getUpperBoundClass(type);
            if (Map.class.isAssignableFrom(rawType)) {
                walkHandler.beforeObject(null, type, contextStack);
                walkHandler.afterObject(null, type, contextStack);
            } else if (Iterable.class.isAssignableFrom(rawType) || ArrayKit.isArray(type)) {
                walkHandler.beforeList(null, type, contextStack);
                walkHandler.afterList(null, type, contextStack);
            } else {
                walkHandler.beforeObject(null, type, contextStack);
                walkHandler.afterObject(null, type, contextStack);
            }
        }
    }
}
