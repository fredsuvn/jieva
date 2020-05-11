package xyz.srclab.common.bytecode;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.common.base.Shares;
import xyz.srclab.common.util.string.StringHelper;

/**
 * @author sunqian
 */
public class ByteCodeHelper {

    private static final CharMatcher dotMatcher = Shares.DOT_CHAR_MATCHER;

    public static final String CONSTRUCTOR_NAME = "<init>";

    public static final BType PRIMITIVE_VOID = new BType(void.class);

    public static final BType OBJECT = new BType(Object.class);

    public static final BArrayType OBJECT_ARRAY = new BArrayType(OBJECT, 1);

    public static final BDescribable[] EMPTY_DESCRIBABLE_ARRAY = new BDescribable[0];

    public static final BTypeVariable[] EMPTY_TYPE_VARIABLE_ARRAY = new BTypeVariable[0];

    public static final BDescribable[] OBJECT_ARRAY_PARAMETER = {OBJECT_ARRAY};

    public static final BMethod OBJECT_INIT =
            new BMethod(CONSTRUCTOR_NAME, null, null, null);

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

    public static String getTypeDescriptor(String className) {
        return "L" + dotMatcher.replaceFrom(className, '/') + ";";
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

    public static String getMethodDescriptor(Class<?>[] parameterTypes, Class<?> returnType) {
        if (ArrayUtils.isEmpty(parameterTypes)) {
            return "()" + getTypeDescriptor(returnType);
        }
        String parametersDescriptor =
                StringHelper.join("", parameterTypes, ByteCodeHelper::getTypeDescriptor);
        return "(" + parametersDescriptor + ")" + getTypeDescriptor(returnType);
    }
}
