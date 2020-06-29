package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author sunqian
 */
public class SetConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (to.equals(Collection.class) || to.equals(Set.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, converter);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, converter);
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
        Class<?> rawTo = TypeKit.getRawType(to);
        if (rawTo.equals(Collection.class) || rawTo.equals(Set.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToSet(iterable, to, converter);
            } else if (from.getClass().isArray()) {
                return arrayToSet(from, to, converter);
            }
            return null;
        }
        return null;
    }

    private Set<?> iterableToSet(Iterable<?> iterable, Converter converter) {
        return iterableToSet0(iterable, Object.class, converter);
    }

    private Set<?> iterableToSet(Iterable<?> iterable, Type type, Converter converter) {
        if (type instanceof ParameterizedType) {
            return iterableToSet0(iterable, ((ParameterizedType) type).getActualTypeArguments()[0], converter);
        }
        if (type instanceof WildcardType) {
            return iterableToSet0(iterable, ((WildcardType) type).getUpperBounds()[0], converter);
        }
        throw new UnsupportedOperationException("Unsupported convert type: " + type);
    }

    private Set<?> arrayToSet(Object array, Converter converter) {
        return arrayToSet0(array, Object.class, converter);
    }

    private Set<?> arrayToSet(Object array, Type type, Converter converter) {
        if (type instanceof ParameterizedType) {
            return arrayToSet0(array, ((ParameterizedType) type).getActualTypeArguments()[0], converter);
        }
        if (type instanceof WildcardType) {
            return arrayToSet0(array, ((WildcardType) type).getUpperBounds()[0], converter);
        }
        throw new UnsupportedOperationException("Unsupported convert type: " + type);
    }

    private Set<?> iterableToSet0(Iterable<?> iterable, Type componentType, Converter converter) {
        Set<Object> result = new LinkedHashSet<>();
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

    private Set<?> arrayToSet0(Object array, Type componentType, Converter converter) {
        return iterableToSet0(ArrayKit.asList(array), componentType, converter);
    }
}
