package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
public class SetConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (to.equals(Collection.class)
                || to.equals(Set.class)
                || to.equals(LinkedHashSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, Object.class, converter, LinkedHashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, Object.class, converter, LinkedHashSet::new);
            }
            return null;
        }
        if (to.equals(HashSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, Object.class, converter, HashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, Object.class, converter, HashSet::new);
            }
            return null;
        }
        if (to.equals(TreeSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, Object.class, converter, TreeSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, Object.class, converter, i -> new TreeSet<>());
            }
            return null;
        }
        return null;
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof Class) {
            return convert(from, (Class<?>) to, converter);
        }
        if (!(to instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType setType = (ParameterizedType) to;
        Class<?> rawSetType = TypeKit.getRawType(setType.getRawType());
        if (rawSetType.equals(Collection.class)
                || rawSetType.equals(Set.class)
                || rawSetType.equals(LinkedHashSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, setType.getActualTypeArguments()[0], converter, LinkedHashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, setType.getActualTypeArguments()[0], converter, LinkedHashSet::new);
            }
            return null;
        }
        if (rawSetType.equals(HashSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, setType.getActualTypeArguments()[0], converter, HashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, setType.getActualTypeArguments()[0], converter, HashSet::new);
            }
            return null;
        }
        if (rawSetType.equals(TreeSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, setType.getActualTypeArguments()[0], converter, TreeSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, setType.getActualTypeArguments()[0], converter, i -> new TreeSet<>());
            }
            return null;
        }
        return null;
    }

    private Set<?> iterableToSet(
            Iterable<?> iterable, Type componentType, Converter converter, Supplier<Set<Object>> setSupplier) {
        Set<Object> result = setSupplier.get();
        for (@Nullable Object o : iterable) {
            if (o == null) {
                result.add(null);
            } else {
                Object value = converter.convert(o, componentType);
                result.add(value);
            }
        }
        return result;
    }

    private Set<?> arrayToSet(
            Object array, Type componentType, Converter converter, Function<Integer, Set<Object>> setFunction) {
        List<Object> list = ArrayKit.asList(array);
        return iterableToSet(list, componentType, converter, () -> setFunction.apply(list.size()));
    }
}
