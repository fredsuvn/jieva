package xyz.srclab.common.reflect;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.base.Loader;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.base.Format;
import xyz.srclab.common.lang.key.Key;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * @author sunqian
 */
public class ClassKit {

    public static <T> T newInstance(String className) {
        @Nullable Class<?> cls = Loader.loadClass(className);
        Check.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(String className, ClassLoader classLoader) {
        @Nullable Class<?> cls = Loader.loadClass(className, classLoader);
        Check.checkArguments(cls != null, "Class not found: " + className);
        return newInstance(cls);
    }

    public static <T> T newInstance(Class<?> cls) {
        @Nullable Constructor<?> constructor = ConstructorKit.getConstructor(cls);
        Check.checkArguments(constructor != null, "Constructor not found: " + cls);
        return Cast.as(ConstructorInvoker.of(constructor).invoke());
    }

    public static <T> T newInstance(Class<?> cls, Class<?>[] parameterTypes, Object[] arguments) {
        @Nullable Constructor<?> constructor = ConstructorKit.getConstructor(cls, parameterTypes);
        Check.checkArguments(constructor != null, "Constructor not found: " + cls);
        return Cast.as(ConstructorInvoker.of(constructor).invoke(arguments));
    }

    public static Type getGenericSuperclass(Class<?> cls, Class<?> targetClass) {
        return GenericSuperclassFinder.find(cls, targetClass);
    }

    public static Type getActualType(Type type, Class<?> userClass, Class<?> declaringClass) {
        return ActualTypeFinder.find(type, userClass, declaringClass);
    }

    public static Class<?> toWrapper(Class<?> primitive) {
        @Nullable Class<?> wrapper = WrapperFinder.find(primitive);
        return wrapper == null ? primitive : wrapper;
    }

    private static final class GenericSuperclassFinder {

        public static Type find(Class<?> cls, Class<?> targetClass) {
            if (!targetClass.isAssignableFrom(cls)) {
                throw new IllegalArgumentException("Target class must be super class of given class");
            }
            return find0(cls, targetClass);
        }

        private static Type find0(Class<?> cls, Class<?> targetClass) {
            Type current = cls;
            do {
                Class<?> currentClass = TypeKit.getRawType(current);
                if (targetClass.equals(currentClass)) {
                    return current;
                }
                current = currentClass.getGenericSuperclass();
            } while (current != null);
            throw new IllegalStateException("Unexpected error: cls = " + cls + ", targetClass = " + targetClass);
        }
    }

    private static final class ActualTypeFinder {

        private static final Cache<Key, Type> cache = Cache.newL2();

        public static Type find(Type type, Class<?> userClass, Class<?> declaringClass) {
            return cache.getNonNull(
                    Key.of(type, userClass, declaringClass),
                    k -> find0(type, userClass, declaringClass)
            );
        }

        private static Type find0(Type type, Class<?> userClass, Class<?> declaringClass) {
            if (type instanceof TypeVariable) {
                Type genericSuperclass = ClassKit.getGenericSuperclass(userClass, declaringClass);
                if (!(genericSuperclass instanceof ParameterizedType)) {
                    throw new IllegalStateException(
                            Format.fastFormat("Cannot find actual type \"{}\" from {} to {}",
                                    type, declaringClass, userClass));
                }
                TypeVariable<?>[] typeVariables = declaringClass.getTypeParameters();
                int index = ArrayUtils.indexOf(typeVariables, type);
                if (index < 0) {
                    throw new IllegalStateException(
                            Format.fastFormat("Cannot find actual type \"{}\" from {} to {}",
                                    type, declaringClass, userClass));
                }
                return ((ParameterizedType) genericSuperclass).getActualTypeArguments()[index];
            }
            return type;
        }
    }

    private static final class WrapperFinder {

        private static final Map<Class<?>, Class<?>> table = MapKit.pairToMap(WrapperClassTable.TABLE);

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
