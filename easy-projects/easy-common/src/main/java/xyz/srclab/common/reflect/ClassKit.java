package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Loader;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.invoke.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sunqian
 */
public class ClassKit {

    public static <T> T newInstance(String className) {
        @Nullable Class<?> cls = Loader.loadClass(className);
        Checker.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(String className, ClassLoader classLoader) {
        @Nullable Class<?> cls = Loader.loadClass(className, classLoader);
        Checker.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(Class<?> cls) {
        @Nullable Constructor<?> constructor = ConstructorKit.getConstructor(cls);
        Checker.checkArguments(constructor != null, "Constructor not found: " + cls);
        return Cast.as(ConstructorInvoker.of(constructor).invoke());
    }

    public static <T> T newInstance(Class<?> cls, Class<?>[] parameterTypes, Object[] arguments) {
        @Nullable Constructor<?> constructor = ConstructorKit.getConstructor(cls, parameterTypes);
        Checker.checkArguments(constructor != null, "Constructor not found: " + cls);
        return Cast.as(ConstructorInvoker.of(constructor).invoke(arguments));
    }

    public static Type getGenericSuperclass(Class<?> cls, Class<?> target) {
        Checker.checkArguments(
                target.isAssignableFrom(cls),
                target + " is not parent of " + cls
        );
        @Nullable Type result = GenericSuperclassFinder.find(cls, target);
        Checker.checkState(
                result != null, "Unexpected error: cls = " + cls + ", target = " + target);
        return result;
    }

    public static Class<?> toWrapper(Class<?> primitive) {
        @Nullable Class<?> wrapper = WrapperFinder.find(primitive);
        return wrapper == null ? primitive : wrapper;
    }

    private static final class GenericSuperclassFinder {

        //private static final Cache<Key, Type> cache = Cache.newL2();

        @Nullable
        public static Type find(Class<?> cls, Class<?> targetSuperClass) {
            return find0(cls, targetSuperClass);
        }

        @Nullable
        private static Type find0(Class<?> cls, Class<?> superClass) {
            Type current = cls;
            do {
                Class<?> currentClass = TypeKit.getRawType(current);
                if (superClass.equals(currentClass)) {
                    return current;
                }
                current = currentClass.getGenericSuperclass();
            } while (current != null);
            return null;
        }
    }

    private static final class WrapperFinder {

        private static final Map<Class<?>, Class<?>> table = MapKit.pairToMap((Object[]) WrapperClassTable.TABLE);

        @Nullable
        public static Class<?> find(Class<?> primitive) {
            return table.get(primitive);
        }

        private static final class WrapperClassTable {

            private static final Class<?>[] TABLE = {
                    boolean.class, Boolean.class,
                    byte.class, Byte.class,
                    short.class, Short.class,
                    char.class, Character.class,
                    int.class, Integer.class,
                    long.class, Long.class,
                    float.class, Float.class,
                    double.class, Double.class,
                    void.class, Void.class,
            };
        }
    }
}
