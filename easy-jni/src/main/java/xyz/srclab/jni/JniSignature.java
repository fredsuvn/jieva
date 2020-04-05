package xyz.srclab.jni;

import java.lang.reflect.Method;

public class JniSignature {

    public static String sign(Class<?> cls) {
        if (cls.isPrimitive()) {
            return signPrimitive(cls);
        }
        if (void.class.equals(cls)) {
            // Java doc doesn't say whether void is primitive,
            // so here may run or never run.
            return "V";
        }
        if (cls.isArray()) {
            return signArray(cls);
        }
        return "L" + cls.getName().replaceAll("\\.", "/") + ";";
    }

    public static String signPrimitive(Class<?> primitiveClass) {
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

    public static String signArray(Class<?> arrayClass) {
        return "[" + sign(arrayClass.getComponentType());
    }

    public static String signMethod(Method method) {
        return signMethod(method.getParameterTypes(), method.getReturnType());
    }

    public static String signMethod(Class<?>[] parameterTypes, Class<?> returnType) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for (Class<?> parameterType : parameterTypes) {
            buf.append(sign(parameterType));
        }
        buf.append(")");
        buf.append(returnType);
        return buf.toString();
    }
}
