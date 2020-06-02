package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Field;

public class FieldKit {

    @Nullable
    public static Field getField(Class<?> cls, String name) {
        try {
            return cls.getField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    @Nullable
    public static Field getDeclaredField(Class<?> cls, String name) {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
