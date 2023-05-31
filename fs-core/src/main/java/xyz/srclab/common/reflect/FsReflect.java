package xyz.srclab.common.reflect;

import xyz.srclab.common.base.FsString;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Reflection utilities.
 *
 * @author fredsuvn
 */
public class FsReflect {

    /**
     * Returns last name of given class. The last name is sub-string after last dot, for example:
     * <p>
     * {@code String} is last name of {@code java.lang.String}.
     *
     * @param cls given class
     */
    public static String getLastName(Class<?> cls) {
        String name = cls.getName();
        int index = FsString.lastIndexOf(name, ".");
        if (index < 0 || index >= name.length() - 1) {
            return name;
        }
        return name.substring(index + 1);
    }

    /**
     * Returns raw type of given type, the given type must be a Class or ParameterizedType.
     *
     * @param type given type
     */
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        throw new IllegalArgumentException("Given type must be Class or ParameterizedType.");
    }

    /**
     * Returns wrapper class if given class is primitive, else return itself.
     *
     * @param cls given class
     */
    public static Class<?> toWrapperClass(Class<?> cls) {
        if (cls.isPrimitive()) {
            if (Objects.equals(cls, boolean.class)) {
                return Boolean.class;
            }
            if (Objects.equals(cls, byte.class)) {
                return Byte.class;
            }
            if (Objects.equals(cls, short.class)) {
                return Short.class;
            }
            if (Objects.equals(cls, char.class)) {
                return Character.class;
            }
            if (Objects.equals(cls, int.class)) {
                return Integer.class;
            }
            if (Objects.equals(cls, long.class)) {
                return Long.class;
            }
            if (Objects.equals(cls, float.class)) {
                return Float.class;
            }
            if (Objects.equals(cls, double.class)) {
                return Double.class;
            }
            if (Objects.equals(cls, void.class)) {
                return Void.class;
            }
        }
        return cls;
    }

    /**
     * Returns whether given class can be assigned by given class 2.
     *
     * @param cls  given class
     * @param cls2 given class 2
     */
    public static boolean canAssignedBy(Class<?> cls, Class<?> cls2) {
        if (cls.isAssignableFrom(cls2)) {
            return true;
        }
        return Objects.equals(toWrapperClass(cls), toWrapperClass(cls2));
    }

    //public static List<Type> getActualTypeArguments(Type subType, Class<?> superType) {
    //    Map<Type, Type> typeMap = new HashMap<>();
    //    if (subType instanceof Class) {
    //        Class<?> subClass = (Class<?>) subType;
    //        Type genericSuperClass = subClass.getGenericSuperclass();
    //
    //    }
    //}

    private static void getActualTypeArguments0() {

    }
}
