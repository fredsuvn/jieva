package xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.common.exception.ShouldNotException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class JavaEnums {

    @Nullable
    static <T extends Enum<T>> T valueOf(@NotNull Class<T> enumType, @NotNull String name) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    static <T extends Enum<T>> T valueOfIgnoreCase(@NotNull Class<T> enumType, @NotNull String name) {
        @Nullable T t = valueOf(enumType, name);
        if (t != null) {
            return t;
        }
        try {
            Method values = enumType.getMethod("values");
            Enum<?>[] array = Anys.as(values.invoke(null));
            for (Enum<?> o : array) {
                if (o.name().equalsIgnoreCase(name)) {
                    return Anys.as(o);
                }
            }
            return null;
        } catch (NoSuchMethodException e) {
            throw new ShouldNotException("values() not found on enum type!");
        } catch (IllegalAccessException e) {
            throw new ShouldNotException("values() cause IllegalAccessException on enum type!");
        } catch (InvocationTargetException e) {
            throw new ShouldNotException("values() cause InvocationTargetException on enum type!");
        }
    }
}
