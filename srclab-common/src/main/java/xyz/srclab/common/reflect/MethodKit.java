package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.ListKit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class MethodKit {

    @Nullable
    public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        try {
            return cls.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nullable
    public static Method getDeclaredMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Immutable
    public static List<Method> getMethods(Class<?> cls) {
        return ListKit.immutable(cls.getMethods());
    }

    @Immutable
    public static List<Method> getDeclaredMethods(Class<?> cls) {
        return ListKit.immutable(cls.getDeclaredMethods());
    }

    @Nullable
    public static Object invoke(Method method, @Nullable Object object, Object... args) {
        return invoke(method, object, args, false);
    }

    @Nullable
    public static Object invoke(Method method, @Nullable Object object, Object[] args, boolean force) {
        try {
            if (force) {
                method.setAccessible(true);
            }
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean canOverride(Method method, Class<?> cls) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)
                || Modifier.isPrivate(modifiers)
                || Modifier.isFinal(modifiers)) {
            return false;
        }
        if (Modifier.isPublic(modifiers)) {
            return true;
        }
        Class<?> declaring = method.getDeclaringClass();
        if (Modifier.isProtected(modifiers)) {
            return declaring.isAssignableFrom(cls);
        }
        return declaring.getPackage().equals(cls.getPackage());
    }
}
