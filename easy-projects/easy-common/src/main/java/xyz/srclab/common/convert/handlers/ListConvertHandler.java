package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.collection.IterableKit;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
public class ListConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (to.isArray()) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToArray(iterable, to, converter);
            } else if (from.getClass().isArray()) {
                return arrayToArray(from, to, converter);
            }
            return null;
        }
        return toList(from, to, converter);
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof Class) {
            return convert(from, (Class<?>) to, converter);
        }
        if (to instanceof GenericArrayType) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToArray(iterable, (GenericArrayType) to, converter);
            } else if (from.getClass().isArray()) {
                return arrayToArray(from, (GenericArrayType) to, converter);
            }
            return null;
        }
        if (to instanceof ParameterizedType) {
            return toList(from, (ParameterizedType) to, converter);
        }
        return null;
    }

    @Nullable
    private List<?> toList(Object from, Class<?> listType, Converter converter) {
        if (listType.equals(List.class) || listType.equals(Iterable.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToList(iterable, converter, LinkedList::new);
            } else if (from.getClass().isArray()) {
                return arrayToList(from, converter, ArrayList::new);
            }
            return null;
        }
        if (listType.equals(ArrayList.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToList(iterable, converter, ArrayList::new);
            } else if (from.getClass().isArray()) {
                return arrayToList(from, converter, ArrayList::new);
            }
            return null;
        }
        if (listType.equals(LinkedList.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToList(iterable, converter, LinkedList::new);
            } else if (from.getClass().isArray()) {
                return arrayToList(from, converter, i -> new LinkedList<>());
            }
            return null;
        }
        return null;
    }

    @Nullable
    private List<?> toList(Object from, ParameterizedType listType, Converter converter) {
        Class<?> rawListType = TypeKit.getRawType(listType);
        if (rawListType.equals(List.class) || rawListType.equals(Iterable.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToList(iterable, listType, converter, LinkedList::new);
            } else if (from.getClass().isArray()) {
                return arrayToList(from, listType, converter, ArrayList::new);
            }
            return null;
        }
        if (rawListType.equals(ArrayList.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToList(iterable, listType, converter, ArrayList::new);
            } else if (from.getClass().isArray()) {
                return arrayToList(from, listType, converter, ArrayList::new);
            }
            return null;
        }
        if (rawListType.equals(LinkedList.class)) {
            if (from instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) from;
                return iterableToList(iterable, listType, converter, LinkedList::new);
            } else if (from.getClass().isArray()) {
                return arrayToList(from, listType, converter, i -> new LinkedList<>());
            }
            return null;
        }
        return null;
    }

    private Object iterableToArray(Iterable<?> iterable, Class<?> arrayClass, Converter converter) {
        return iterableToArray0(iterable, arrayClass.getComponentType(), converter);
    }

    private Object iterableToArray(Iterable<?> iterable, GenericArrayType arrayType, Converter converter) {
        return iterableToArray0(iterable, TypeKit.getRawType(arrayType.getGenericComponentType()), converter);
    }

    private List<?> iterableToList(
            Iterable<?> iterable, Converter converter, Supplier<List<Object>> listSupplier) {
        return iterableToList0(iterable, Object.class, converter, listSupplier);
    }

    private List<?> iterableToList(
            Iterable<?> iterable, ParameterizedType type, Converter converter, Supplier<List<Object>> listSupplier) {
        return iterableToList0(iterable, type.getActualTypeArguments()[0], converter, listSupplier);
    }

    private Object arrayToArray(Object array, Class<?> arrayClass, Converter converter) {
        return arrayToArray0(array, arrayClass.getComponentType(), converter);
    }

    private Object arrayToArray(Object array, GenericArrayType arrayType, Converter converter) {
        return arrayToArray0(array, TypeKit.getRawType(arrayType.getGenericComponentType()), converter);
    }

    private List<?> arrayToList(
            Object array, Converter converter, Function<Integer, List<Object>> listFunction) {
        return arrayToList0(array, Object.class, converter, listFunction);
    }

    private List<?> arrayToList(
            Object array, ParameterizedType type, Converter converter, Function<Integer, List<Object>> listFunction) {
        return arrayToList0(array, type.getActualTypeArguments()[0], converter, listFunction);
    }

    private List<?> iterableToList0(
            Iterable<?> iterable, Type componentType, Converter converter, Supplier<List<Object>> listSupplier) {
        List<Object> result = listSupplier.get();
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

    private List<?> arrayToList0(
            Object array, Type componentType, Converter converter, Function<Integer, List<Object>> listFunction) {
        List<Object> list = ArrayKit.asList(array);
        return iterableToList0(list, componentType, converter, () -> listFunction.apply(list.size()));
    }

    private Object iterableToArray0(Iterable<?> iterable, Class<?> componentType, Converter converter) {
        List<?> list = IterableKit.asList(iterable);
        Object result = ArrayKit.newArray(componentType, list.size());
        int i = 0;
        if (result instanceof Object[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    Object value = converter.convert(o, componentType);
                    ((Object[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof boolean[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    boolean value = converter.convert(o, boolean.class);
                    ((boolean[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof byte[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    byte value = converter.convert(o, byte.class);
                    ((byte[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof short[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    short value = converter.convert(o, short.class);
                    ((short[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof char[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    char value = converter.convert(o, char.class);
                    ((char[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof int[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    int value = converter.convert(o, int.class);
                    ((int[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof long[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    long value = converter.convert(o, long.class);
                    ((long[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof float[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    float value = converter.convert(o, float.class);
                    ((float[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        if (result instanceof double[]) {
            for (@Nullable Object o : list) {
                if (o != null) {
                    double value = converter.convert(o, double.class);
                    ((double[]) result)[i] = value;
                }
                i++;
            }
            return result;
        }
        throw new IllegalArgumentException("Unknown array type: " + result.getClass());
    }

    private Object arrayToArray0(Object array, Class<?> componentType, Converter converter) {
        return iterableToArray0(ArrayKit.asList(array), componentType, converter);
    }
}
