package xyz.srclab.common.invoke.provider.methodhandle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author sunqian
 */
final class MethodHandleSupport {

    static MethodHandle getMethodHandle(Method method) {
        MethodType methodType;
        switch (method.getParameterCount()) {
            case 0:
                methodType = MethodType.methodType(method.getReturnType());
                break;
            case 1:
                methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes()[0]);
                break;
            default:
                methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        }
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return Modifier.isStatic(method.getModifiers()) ?
                    lookup.findStatic(method.getDeclaringClass(), method.getName(), methodType)
                    :
                    lookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    static MethodHandle getMethodHandle(Constructor<?> constructor) {
        MethodType methodType;
        switch (constructor.getParameterCount()) {
            case 0:
                methodType = MethodType.methodType(void.class);
                break;
            case 1:
                methodType = MethodType.methodType(void.class, constructor.getParameterTypes()[0]);
                break;
            default:
                methodType = MethodType.methodType(void.class, constructor.getParameterTypes());
        }
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return lookup.findConstructor(constructor.getDeclaringClass(), methodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
