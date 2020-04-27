package xyz.srclab.common.reflect.signature;

import xyz.srclab.common.bytecode.ByteCodeHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class SignatureHelper {

    public static String signClass(Class<?> cls) {
        return ByteCodeHelper.getDescriptor(cls);
    }

    public static String signPrimitiveClass(Class<?> primitiveClass) {
        return ByteCodeHelper.getPrimitiveDescriptor(primitiveClass);
    }

    public static String signArrayClass(Class<?> arrayClass) {
        return ByteCodeHelper.getArrayDescriptor(arrayClass);
    }

    public static String signConstructor(Constructor<?> constructor) {
        return signConstructor(constructor.getDeclaringClass(), constructor.getParameterTypes());
    }

    public static String signConstructor(Class<?> cls, Class<?>... parameterTypes) {
        return cls.getName() +
                "(" +
                signClassArray(parameterTypes) +
                ")";
    }

    public static String signMethod(Method method) {
        return signMethod(method.getName(), method.getParameterTypes());
    }

    public static String signMethod(String methodName, Class<?>... parameterTypes) {
        return methodName +
                "(" +
                signClassArray(parameterTypes) +
                ")";
    }

    private static String signClassArray(Class<?>... classes) {
        switch (classes.length) {
            case 0:
                return "";
            case 1:
                return classes[0].getName();
            default:
                StringBuilder buf = new StringBuilder();
                for (Class<?> cls : classes) {
                    buf.append(cls.getName());
                    buf.append(",");
                }
                buf.delete(buf.length() - 1, buf.length());
                return buf.toString();
        }
    }
}
