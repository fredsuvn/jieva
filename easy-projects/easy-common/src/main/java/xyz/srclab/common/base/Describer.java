package xyz.srclab.common.base;

import com.google.common.base.Joiner;
import xyz.srclab.common.array.ArrayHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public class Describer {

    private static final Joiner PARAMETER_TYPES_JOINER = Joiner.on(", ");

    public static String constructorToString(Constructor<?> constructor) {
        return constructorToString(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }

    public static String constructorToString(Class<?> declaringClass, Class<?>... parameterTypes) {
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
        return PARAMETER_TYPES_JOINER.join(
                ArrayHelper.buildArray(new String[parameterTypes.length], i -> parameterTypes[i].getName())
        );
    }
}
