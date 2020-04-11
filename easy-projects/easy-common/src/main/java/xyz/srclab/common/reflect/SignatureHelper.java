package xyz.srclab.common.reflect;

import java.lang.reflect.Method;

public class SignatureHelper {

    public static String signClass(Class<?> cls) {
        if (cls.isPrimitive()) {
            return signPrimitiveClass(cls);
        }
        if (cls.isArray()) {
            return signArrayClass(cls);
        }
        //if (void.class.equals(cls)) {
        // Java doc doesn't say whether void is primitive,
        // so here may run or never run.
        //return "V";
        //}
        return "L" + cls.getName().replaceAll("\\.", "/") + ";";
    }

    public static String signPrimitiveClass(Class<?> primitiveClass) {
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

    public static String signArrayClass(Class<?> arrayClass) {
        return "[" + signClass(arrayClass.getComponentType());
    }

    public static String signMethod(Method method) {
        return signMethod(method.getName(), method.getParameterTypes());
    }

    public static String signMethod(String methodName, Class<?>[] parameterTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append(methodName);
        buf.append("(");
        switch (parameterTypes.length) {
            case 0:
                buf.append(")");
                return buf.toString();
            case 1:
                buf.append(parameterTypes[0].getName());
                buf.append(")");
                return buf.toString();
            default:
                for (Class<?> parameterType : parameterTypes) {
                    buf.append(parameterType.getName());
                    buf.append(",");
                }
                buf.replace(buf.length() - 1, buf.length(), ")");
                return buf.toString();
        }
    }
}
