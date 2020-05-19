package xyz.srclab.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class NullMember {

    private static final Constructor<?> NULL_CONSTRUCTOR;

    private static final Method NULL_METHOD;

    private static final Type NULL_TYPE = new Type() {

        @Override
        public String toString() {
            return "NullType";
        }
    };

    private static final Field NULL_FIELD;

    public static Constructor<?> asConstructor() {
        return NULL_CONSTRUCTOR;
    }

    public static Method asMethod() {
        return NULL_METHOD;
    }

    public static Type asType() {
        return NULL_TYPE;
    }

    public static Field asField() {
        return NULL_FIELD;
    }

    private static void invokeNull() {
        throw new NullPointerException();
    }

    static {
        try {
            NULL_CONSTRUCTOR = NullMember.class.getDeclaredConstructor();
            NULL_METHOD = NullMember.class.getDeclaredMethod("invokeNull");
            NULL_FIELD = NullMember.class.getDeclaredField("NULL_FIELD");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
