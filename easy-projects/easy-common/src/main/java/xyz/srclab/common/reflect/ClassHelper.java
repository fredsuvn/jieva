package xyz.srclab.common.reflect;

import org.apache.commons.lang3.ClassUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Loader;
import xyz.srclab.common.base.Null;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.lang.Key;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 * @author sunqian
 */
public class ClassHelper {

    private static final Cache<Key, Type> genericSuperClassCache = Cache.newGcThreadLocalL2();

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
        @Nullable Constructor<?> constructor = ConstructorHelper.getConstructor(cls);
        Checker.checkArguments(constructor != null, "Constructor not found: " + cls);
        return (T) ConstructorInvoker.of(constructor).invoke();
    }

    public static <T> T newInstance(Class<?> cls, Class<?>[] parameterTypes, Object[] arguments) {
        @Nullable Constructor<?> constructor = ConstructorHelper.getConstructor(cls, parameterTypes);
        Checker.checkArguments(constructor != null, "Constructor not found: " + cls);
        return (T) ConstructorInvoker.of(constructor).invoke(arguments);
    }

    public static boolean isBasic(Object any) {
        return any instanceof CharSequence
                || any instanceof Number
                || any instanceof Type
                || any instanceof Date
                || any instanceof Temporal;
    }

    public static boolean isAssignable(Object from, Class<?> to) {
        Class<?> fromType = from instanceof Class<?> ? (Class<?>) from : from.getClass();
        return ClassUtils.isAssignable(fromType, to);
    }

    @Nullable
    public static Type getGenericSuperclass(Class<?> cls, Class<?> targetSuperClass) {
        Checker.checkArguments(
                targetSuperClass.isAssignableFrom(cls),
                targetSuperClass + "is not parent of " + cls
        );
        Type result = genericSuperClassCache.getNonNull(
                Key.from(cls, targetSuperClass),
                k -> getGenericSuperclass0(cls, targetSuperClass)
        );
        return Null.isNull(result) ? null : result;
    }

    private static Type getGenericSuperclass0(Class<?> cls, Class<?> superClass) {
        Type current = cls;
        do {
            Class<?> currentClass = TypeHelper.getRawType(current);
            if (superClass.equals(currentClass)) {
                return current;
            }
            current = currentClass.getGenericSuperclass();
        } while (current != null);
        return Null.asType();
    }

    public static Class<?> primitiveToWrapper(Class<?> primitive) {
        if (boolean.class.equals(primitive)) {
            return Boolean.class;
        }
        if (byte.class.equals(primitive)) {
            return Byte.class;
        }
        if (short.class.equals(primitive)) {
            return Short.class;
        }
        if (char.class.equals(primitive)) {
            return Character.class;
        }
        if (int.class.equals(primitive)) {
            return Integer.class;
        }
        if (long.class.equals(primitive)) {
            return Long.class;
        }
        if (float.class.equals(primitive)) {
            return Float.class;
        }
        if (double.class.equals(primitive)) {
            return Double.class;
        }
        if (void.class.equals(primitive)) {
            return Void.class;
        }
        throw new IllegalArgumentException("Unknown primitive type: " + primitive);
    }
}
