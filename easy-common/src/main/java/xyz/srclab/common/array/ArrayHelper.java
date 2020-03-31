package xyz.srclab.common.array;

import xyz.srclab.annotation.WriteReturn;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ArrayHelper {

    public static <T> T[] newArray(Class<?> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    public static <T> T[] newArray(Type componentType, int length) {
        return newArray(TypeHelper.getRawClass(componentType), length);
    }

    public static <T, R> R[] map(T[] source, @WriteReturn R[] dest, Function<T, R> mapper) {
        int mapLength = Math.min(source.length, dest.length);
        for (int i = 0; i < mapLength; i++) {
            dest[i] = mapper.apply(source[i]);
        }
        return dest;
    }

    public static <T, R> R[] map(T[] array, Class<R> resultComponentType, Function<T, R> mapper) {
        R[] result = newArray(resultComponentType, array.length);
        for (int i = 0; i < result.length; i++) {
            result[i] = mapper.apply(array[i]);
        }
        return result;
    }

    public static <T, R> R[] map(T[] array, Type resultComponentType, Function<T, R> mapper) {
        return map(array, TypeHelper.getRawClass(resultComponentType), mapper);
    }

    public static <T, R> R[] map(T[] array, TypeRef<R> resultComponentType, Function<T, R> mapper) {
        return map(array, resultComponentType.getRawType(), mapper);
    }

    public static <T, R> R[] toArray(Iterable<T> source, @WriteReturn R[] dest, Function<T, R> mapper) {
        int i = 0;
        for (T t : source) {
            if (i >= dest.length) {
                break;
            }
            dest[i] = mapper.apply(t);
        }
        return dest;
    }

    public static <T, R> R[] toArray(Iterable<T> source, Class<R> resultComponentType, Function<T, R> mapper) {
        List<R> list = new LinkedList<>();
        for (T t : source) {
            list.add(mapper.apply(t));
        }
        return list.toArray(newArray(resultComponentType, list.size()));
    }

    public static <T, R> R[] toArray(Iterable<T> source, Type resultComponentType, Function<T, R> mapper) {
        return toArray(source, TypeHelper.getRawClass(resultComponentType), mapper);
    }

    public static <T, R> R[] toArray(Iterable<T> source, TypeRef<R> resultComponentType, Function<T, R> mapper) {
        return toArray(source, resultComponentType.getRawType(), mapper);
    }
}
