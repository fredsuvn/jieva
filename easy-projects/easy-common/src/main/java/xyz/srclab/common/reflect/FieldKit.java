package xyz.srclab.common.reflect;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.ListKit;

import java.lang.reflect.Field;
import java.util.List;

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

    @Immutable
    public static List<Field> getFields(Class<?> cls) {
        return ListKit.immutable(cls.getFields());
    }

    @Nullable
    public static Object getValue(Field field, @Nullable Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void setValue(Field field, @Nullable Object object, @Nullable Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
