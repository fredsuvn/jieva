package xyz.srclab.common.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class NullRole {

    private static final Constructor<?> NULL_CONSTRUCTOR;

    private static final Method NULL_METHOD;

    private static final Type NULL_TYPE = new Type() {

        @Override
        public String toString() {
            return "NullType";
        }
    };

    public static Constructor<?> getNullConstructor() {
        return NULL_CONSTRUCTOR;
    }

    public static Method getNullMethod() {
        return NULL_METHOD;
    }

    public static Type getNullType() {
        return NULL_TYPE;
    }

    private static void nullMethod() {
        throw new IllegalStateException("This method should never be invoked!");
    }

    static {
        try {
            NULL_CONSTRUCTOR = NullRole.class.getConstructor();
            NULL_METHOD = NullRole.class.getMethod("nullMethod");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
