package xyz.srclab.common.bytecode;

import com.google.common.base.CharMatcher;

/**
 * @author sunqian
 */
public class ByteCodeHelper {

    private static final CharMatcher charMatcher = CharMatcher.is('.');

    public static String getInternalName(Class<?> type) {
        return charMatcher.replaceFrom(type.getName(), '/');
    }

    public static String getDescriptor(Class<?> type) {
        if (type.isPrimitive()) {
            return getPrimitiveDescriptor(type);
        }
        if (type.isArray()) {
            return getArrayDescriptor(type);
        }
        //if (void.class.equals(cls)) {
        // Java doc doesn't say whether void is primitive,
        // so here may run or never run.
        //return "V";
        //}
        return "L" + charMatcher.replaceFrom(type.getName(), '/') + ";";
    }

    public static String getPrimitiveDescriptor(Class<?> primitiveType) {
        if (void.class.equals(primitiveType)) {
            // Note java doc doesn't say whether void is primitive,
            // but in performance it is, so here it is.
            return "V";
        }
        if (int.class.equals(primitiveType)) {
            return "I";
        }
        if (long.class.equals(primitiveType)) {
            return "J";
        }
        if (boolean.class.equals(primitiveType)) {
            return "Z";
        }
        if (char.class.equals(primitiveType)) {
            return "C";
        }
        if (byte.class.equals(primitiveType)) {
            return "B";
        }
        if (double.class.equals(primitiveType)) {
            return "D";
        }
        if (float.class.equals(primitiveType)) {
            return "F";
        }
        if (short.class.equals(primitiveType)) {
            return "S";
        }
        throw new IllegalArgumentException("Type is not primitive: " + primitiveType);
    }

    public static String getArrayDescriptor(Class<?> arrayType) {
        return "[" + getDescriptor(arrayType.getComponentType());
    }

    public static String getMethodDescriptor(Class<?> returnType, Class<?>... parameterTypes) {
        StringBuilder buf = new StringBuilder();
        for (Class<?> parameterType : parameterTypes) {
            buf.append(getDescriptor(parameterType));
        }
        return "(" + buf + ")" + getDescriptor(returnType);
    }
}
