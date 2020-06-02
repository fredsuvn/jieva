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

    public static boolean canOverride(Method method) {
        int modifiers = method.getModifiers();
        return !method.isBridge()
                && !Modifier.isStatic(modifiers)
                && !Modifier.isFinal(modifiers)
                && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
    }
}
