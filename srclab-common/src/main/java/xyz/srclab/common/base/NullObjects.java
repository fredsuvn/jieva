package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class NullObjects {

    public static final Object OBJECT = new Object();

    public static final String STRING = "";

    public static final Set<?> SET = Collections.unmodifiableSet(Collections.emptySet());

    public static final List<?> LIST = Collections.unmodifiableList(Collections.emptyList());

    public static final Collection<?> COLLECTION = SET;

    public static final Map<?, ?> MAP = Collections.unmodifiableMap(Collections.emptyMap());

    public static final Class<?> CLASS = NullClass.class;

    public static final Type TYPE = CLASS;

    public static final Constructor<?> CONSTRUCTOR;

    public static final Method METHOD;

    public static final Field FIELD;

    static {
        try {
            CONSTRUCTOR = NullClass.class.getDeclaredConstructor();
            FIELD = NullClass.class.getDeclaredField("nullField");
            METHOD = NullClass.class.getDeclaredMethod("nullMethod");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final class NullClass {

        private final @Nullable NullClass nullField = null;

        public void nullMethod() {
            throw new NullPointerException();
        }
    }
}
