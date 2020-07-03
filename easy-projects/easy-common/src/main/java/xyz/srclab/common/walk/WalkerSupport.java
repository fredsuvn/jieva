package xyz.srclab.common.walk;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.object.UnitPredicate;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sunqian
 */
final class WalkerSupport {

    private static final class WalkerImpl implements Walker {

        private final WalkHandler walkHandler;
        private final UnitPredicate unitPredicate;

        private WalkerImpl(WalkHandler walkHandler, UnitPredicate unitPredicate) {
            this.walkHandler = walkHandler;
            this.unitPredicate = unitPredicate;
        }

        @Override
        public void walk(Object any, Type type) {
            if (any instanceof Iterable) {
                walkHandler.beforeList(any, type);
                Iterable<?> iterable = (Iterable<?>) any;
                for (@Nullable Object o : iterable) {
                    if (o == null) {
                        walkHandler.doUnit(null, Object.class);
                    } else {
                        walk(o);
                    }
                }
                walkHandler.afterList(any, type);
            } else if (any instanceof Map) {
                walkHandler.beforeRecord(any, type);
                Map<?,?> map = (Map<?, ?>) any;
                map.forEach((k,v)->{
                    walkHandler.doEntry(k, );
                });
                walkHandler.afterRecord(any, type);
            }
        }

        private Type getType(@Nullable Object any) {
            return any == null ? Object.class : any.getClass();
        }
    }
}
