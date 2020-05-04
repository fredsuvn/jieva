package xyz.srclab.common.bytecode;

import com.google.common.base.CharMatcher;
import xyz.srclab.common.base.Shares;

/**
 * @author sunqian
 */
public class ByteCodeHelper {

    private static final CharMatcher dotMatcher = Shares.DOT_CHAR_MATCHER;

    public static String getTypeInternalName(Class<?> type) {
        return getTypeInternalName(type.getName());
    }

    public static String getTypeInternalName(String className) {
        return dotMatcher.replaceFrom(className, '/');
    }

    public static String getTypeDescriptor(Class<?> type) {
        if (type.isPrimitive()) {
            return getPrimitiveTypeDescriptor(type);
        }
        if (type.isArray()) {
            return getArrayTypeDescriptor(type);
        }
        //if (void.class.equals(cls)) {
        // Java doc doesn't say whether void is primitive,
        // so here may run or never run.
        //return "V";
        //}
        return "L" + dotMatcher.replaceFrom(type.getName(), '/') + ";";
    }

    public static String getPrimitiveTypeDescriptor(Class<?> primitiveType) {
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

    public static String getArrayTypeDescriptor(Class<?> arrayType) {
        return "[" + getTypeDescriptor(arrayType.getComponentType());
    }

    public static String getMethodDescriptor(Class<?> returnType, Class<?>... parameterTypes) {
        StringBuilder buf = new StringBuilder();
        for (Class<?> parameterType : parameterTypes) {
            buf.append(getTypeDescriptor(parameterType));
        }
        return "(" + buf + ")" + getTypeDescriptor(returnType);
    }
}
