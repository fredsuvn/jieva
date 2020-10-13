package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.As;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.base.Loader;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.lang.finder.Finder;

import java.lang.reflect.Constructor;

/**
 * @author sunqian
 */
public class ClassKit {

    public static <T> T newInstance(String className) {
        @Nullable Class<?> cls = Loader.findClass(className);
        Check.checkArgument(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(String className, ClassLoader classLoader) {
        @Nullable Class<?> cls = Loader.findClass(className, classLoader);
        Check.checkArgument(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(Class<?> cls) {
        @Nullable Constructor<?> constructor = ConstructorKit.getConstructor(cls);
        Check.checkArgument(constructor != null, "Constructor not found: " + cls);
        return As.notNull(ConstructorInvoker.of(constructor).invoke());
    }

    public static <T> T newInstance(Class<?> cls, Class<?>[] parameterTypes, Object[] arguments) {
        @Nullable Constructor<?> constructor = ConstructorKit.getConstructor(cls, parameterTypes);
        Check.checkArgument(constructor != null, "Constructor not found: " + cls);
        return As.notNull(ConstructorInvoker.of(constructor).invoke(arguments));
    }

    public static Class<?> toWrapper(Class<?> primitive) {
        @Nullable Class<?> wrapper = WrapperFinder.find(primitive);
        return wrapper == null ? primitive : wrapper;
    }

    private static final class WrapperFinder {

        private static final Finder<Class<?>, Class<?>> finder = Finder.pairHashFinder(WrapperClasses.TABLE);

        @Nullable
        public static Class<?> find(Class<?> primitive) {
            return finder.find(primitive);
        }

        private static final class WrapperClasses {

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
