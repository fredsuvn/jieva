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
                return iterableToSet(iterable, converter, LinkedHashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, converter, LinkedHashSet::new);
            }
            return null;
        }
        if (to.equals(HashSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, converter, HashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, converter, HashSet::new);
            }
            return null;
        }
        if (to.equals(TreeSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, converter, TreeSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, converter, i -> new TreeSet<>());
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
        ParameterizedType parameterizedTo = (ParameterizedType) to;
        Class<?> rawTo = TypeKit.getRawType(to);
        if (rawTo.equals(Collection.class)
                || rawTo.equals(Set.class)
                || rawTo.equals(LinkedHashSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, parameterizedTo, converter, LinkedHashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, parameterizedTo, converter, LinkedHashSet::new);
            }
            return null;
        }
        if (rawTo.equals(HashSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, parameterizedTo, converter, HashSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, parameterizedTo, converter, HashSet::new);
            }
            return null;
        }
        if (rawTo.equals(TreeSet.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, parameterizedTo, converter, TreeSet::new);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, parameterizedTo, converter, i -> new TreeSet<>());
            }
            return null;
        }
        return null;
    }

    private Set<?> iterableToSet(
            Iterable<?> iterable, Converter converter, Supplier<Set<Object>> setSupplier) {
        return iterableToSet0(iterable, Object.class, converter, setSupplier);
    }

    private Set<?> iterableToSet(
            Iterable<?> iterable, ParameterizedType type, Converter converter, Supplier<Set<Object>> setSupplier) {
        return iterableToSet0(
                iterable, ((ParameterizedType) type).getActualTypeArguments()[0], converter, setSupplier);
    }

    private Set<?> arrayToSet(
            Object array, Converter converter, Function<Integer, Set<Object>> setFunction) {
        return arrayToSet0(array, Object.class, converter, setFunction);
    }

    private Set<?> arrayToSet(
            Object array, ParameterizedType type, Converter converter, Function<Integer, Set<Object>> setFunction) {
        return arrayToSet0(array, ((ParameterizedType) type).getActualTypeArguments()[0], converter, setFunction);
    }

    private Set<?> iterableToSet0(
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

    private Set<?> arrayToSet0(
            Object array, Type componentType, Converter converter, Function<Integer, Set<Object>> setFunction) {
        List<Object> list = ArrayKit.asList(array);
        return iterableToSet0(list, componentType, converter, () -> setFunction.apply(list.size()));
    }
}
