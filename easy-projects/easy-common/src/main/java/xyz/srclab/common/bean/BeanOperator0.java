package xyz.srclab.common.bean;

import xyz.srclab.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 * @author sunqian
 */
final class BeanOperator0 {

    static BeanOperator getDefault() {
        return DefaultOperatorHolder.SINGLETON;
    }

    static BeanOperatorBuilder newOperatorBuilder() {
        return new BeanOperatorBuilder();
    }

    static boolean canResolve(Object object) {
        return AtomicTypeTable.search(object.getClass()) == null;
    }

    private static final class DefaultOperatorHolder {

        private static final BeanOperator SINGLETON = newOperatorBuilder().build();
    }

    private static final class AtomicTypeTable {

        private static final Class<?>[] table = {
                CharSequence.class,
                Number.class,
                Character.class,
                Type.class,
                Date.class,
                Temporal.class,
                Annotation.class,
        };

        @Nullable
        private static Class<?> search(Class<?> type) {
            if (type.isPrimitive()) {
                return type;
            }
            for (Class<?> aClass : table) {
                if (aClass.isAssignableFrom(type)) {
                    return aClass;
                }
            }
            return null;
        }
    }
}
