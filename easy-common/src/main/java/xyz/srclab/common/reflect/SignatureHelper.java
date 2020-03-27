package xyz.srclab.common.reflect;

import xyz.srclab.common.lang.format.FormatHelper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SignatureHelper {

    public static String signature(Class<?> cls) {
        if (cls.isPrimitive()) {
            return signaturePrimitive(cls);
        }
        if (void.class.equals(cls)) {
            // Java doc doesn't say whether void is primitive,
            // so here may run or never run.
            return "V";
        }
        if (cls.isArray()) {
            return signatureArray(cls);
        }
        return "L" + cls.getName().replaceAll("\\.", "/") + ";";
    }

    public static String signaturePrimitive(Class<?> primitiveClass) {
        if (void.class.equals(primitiveClass)) {
            // Note java doc doesn't say whether void is primitive,
            // but in performance it is, so here it is.
            return "V";
        }
        if (int.class.equals(primitiveClass)) {
            return "I";
        }
        if (long.class.equals(primitiveClass)) {
            return "J";
        }
        if (boolean.class.equals(primitiveClass)) {
            return "Z";
        }
        if (char.class.equals(primitiveClass)) {
            return "C";
        }
        if (byte.class.equals(primitiveClass)) {
            return "B";
        }
        if (double.class.equals(primitiveClass)) {
            return "D";
        }
        if (float.class.equals(primitiveClass)) {
            return "F";
        }
        if (short.class.equals(primitiveClass)) {
            return "S";
        }
        throw new IllegalArgumentException("Type is not primitive: " + primitiveClass);
    }

    public static String signatureArray(Class<?> arrayClass) {
        return "[" + signature(arrayClass.getComponentType());
    }

    public static String signatureMethod(Method method) {
        return signatureMethod(method.getParameterTypes(), method.getReturnType());
    }

    public static String signatureMethod(Class<?>[] parameterTypes, Class<?> returnType) {
        return FormatHelper.fastFormat(
                "({}){}",
                Arrays.stream(parameterTypes)
                        .map(SignatureHelper::signature)
                        .collect(Collectors.joining("")),
                signature(returnType)
        );
    }

    public static String signatureMethodWithMethodName(Method method) {
        return signatureMethodWithMethodName(method.getParameterTypes(), method.getReturnType(), method.getName());
    }

    public static String signatureMethodWithMethodName(
            Class<?>[] parameterTypes, Class<?> returnType, String methodName) {
        return "M" + methodName + signatureMethod(parameterTypes, returnType);
    }

    public static String signatureMethodWithClassAndMethodName(Method method) {
        return signatureMethodWithClassAndMethodName(
                method.getDeclaringClass(), method.getParameterTypes(), method.getReturnType(), method.getName());
    }

    public static String signatureMethodWithClassAndMethodName(
            Class<?> declaringClass, Class<?>[] parameterTypes, Class<?> returnType, String methodName) {
        return "M" + signature(declaringClass) + "." + methodName + signatureMethod(parameterTypes, returnType);
    }
}
