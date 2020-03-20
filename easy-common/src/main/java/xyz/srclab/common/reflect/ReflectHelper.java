package xyz.srclab.common.reflect;

import xyz.srclab.common.collection.CollectionHelper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectHelper {

    public static <T> T newInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<Method> getAllMethods(Class<?> cls) {
        List<Method> returned = new LinkedList<>();
        Class<?> current = cls;
        do {
            returned.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        } while (current != null);
        return returned;
    }

    public static List<Method> getAllMethods(Class<?>... classes) {
        return getAllMethods(Arrays.asList(classes));
    }

    public static List<Method> getAllMethods(Iterable<Class<?>> classes) {
        return CollectionHelper.concat(
                CollectionHelper
                        .castCollection(classes)
                        .stream()
                        .map(ReflectHelper::getAllMethods)
                        .collect(Collectors.toList())
        );
    }
}
