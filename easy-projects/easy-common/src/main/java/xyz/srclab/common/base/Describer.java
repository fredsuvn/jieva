package xyz.srclab.common.base;

import xyz.srclab.common.string.StringHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public class Describer {

    public static String describe(Constructor<?> constructor) {
        return describe(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }

    public static String describe(Class<?> declaringClass, Class<?>... parameterTypes) {
        return methodToString(declaringClass, "<init>", parameterTypes);
    }

    public static String methodToString(Method method) {
        return methodToString(method.getDeclaringClass(), method.getName(), method.getParameterTypes());
    }

    public static String methodToString(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) {
        return declaringClass.getName() +
                "." +
                methodName +
                "(" +
                parameterTypesToString(parameterTypes) +
                ")";
    }

    public static String parameterTypesToString(Class<?>... parameterTypes) {
        return StringHelper.join(", ", parameterTypes, Class::getName);
    }
}
