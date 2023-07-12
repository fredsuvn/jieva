package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Convert handler implementation supports converting collections.
 * It supports target type in:
 * <ul>
 *     <li>{@link Iterable}</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link #NOT_SUPPORTED}.
 *
 * @author fredsuvn
 */
public class CollectConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return NOT_SUPPORTED;
        }
        if (targetType instanceof Class) {
            return convertClass(obj, fromType, (Class<?>) targetType, converter);
        }
        if (targetType instanceof ParameterizedType) {
            return convertParameterized(obj, fromType, (ParameterizedType) targetType, converter);
        }
        if (targetType instanceof GenericArrayType) {
            return convertGenericArray(obj, fromType, (GenericArrayType) targetType, converter);
        }
        return NOT_SUPPORTED;
    }

    @Nullable
    private Object convertClass(Object obj, Type fromType, Class<?> targetType, FsConverter converter) {
        if (targetType.isArray()) {
            Class<?> objClass = obj.getClass();
            if (objClass.isArray()) {
                int len = Array.getLength(obj);
                Class<?> componentType = objClass.getComponentType();
                Object array = Array.newInstance(componentType, len);
                if (FsType.isAssignableFrom(Object[].class, fromType)){
                    Object[] oa = (Object[]) obj;
                }
                for (int i = 0; i < len; i++) {
                    Object fromValue = Array.get(obj, i);
                    Object toValue = converter.convert(fromValue, componentType);
                    if (toValue == NOT_SUPPORTED) {
                        return NOT_SUPPORTED;
                    }
                    Array.set(array, i, toValue);
                }
                return array;
            }
        } else if (Objects.equals(targetType, List.class)
            || Objects.equals(targetType, ArrayList.class)
            || Objects.equals(targetType, Collection.class)
            || Objects.equals(targetType, Iterable.class)) {
            if (obj instanceof Collection) {
                Collection<?> collection = (Collection<?>) obj;
                return new ArrayList<>(collection);
            }
            if (obj instanceof Iterable) {
                Iterable<?> it = (Iterable<?>) obj;
                LinkedList<? super Object> list = new LinkedList<>();
                for (Object o : it) {
                    list.add(o);
                }
                return new ArrayList<>(list);
            }
        } else if (Objects.equals(targetType, LinkedList.class)) {
            if (obj instanceof Collection) {
                Collection<?> collection = (Collection<?>) obj;
                return new LinkedList<>(collection);
            }
            if (obj instanceof Iterable) {
                Iterable<?> it = (Iterable<?>) obj;
                LinkedList<? super Object> list = new LinkedList<>();
                for (Object o : it) {
                    list.add(o);
                }
                return list;
            }
        } else if (Objects.equals(targetType, Set.class)
            || Objects.equals(targetType, LinkedHashSet.class)) {
            if (obj instanceof Collection) {
                Collection<?> collection = (Collection<?>) obj;
                return new LinkedHashSet<>(collection);
            }
            if (obj instanceof Iterable) {
                Iterable<?> it = (Iterable<?>) obj;
                LinkedHashSet<? super Object> set = new LinkedHashSet<>();
                for (Object o : it) {
                    set.add(o);
                }
                return set;
            }
        } else if (Objects.equals(targetType, HashSet.class)) {
            if (obj instanceof Collection) {
                Collection<?> collection = (Collection<?>) obj;
                return new HashSet<>(collection);
            }
            if (obj instanceof Iterable) {
                Iterable<?> it = (Iterable<?>) obj;
                HashSet<? super Object> set = new HashSet<>();
                for (Object o : it) {
                    set.add(o);
                }
                return set;
            }
        } else if (Objects.equals(targetType, TreeSet.class)) {
            if (obj instanceof Collection) {
                Collection<?> collection = (Collection<?>) obj;
                return new TreeSet<>(collection);
            }
            if (obj instanceof Iterable) {
                Iterable<?> it = (Iterable<?>) obj;
                TreeSet<? super Object> set = new TreeSet<>();
                for (Object o : it) {
                    set.add(o);
                }
                return set;
            }
        }
        return NOT_SUPPORTED;
    }

    @Nullable
    private Object convertParameterized(Object obj, Type fromType, ParameterizedType targetType, FsConverter converter) {
        return NOT_SUPPORTED;
    }

    @Nullable
    private Object convertGenericArray(Object obj, Type fromType, GenericArrayType targetType, FsConverter converter) {
        return NOT_SUPPORTED;
    }

    @Nullable
    private Object convertArray(Object obj, Type fromType, Type componentType, FsConverter converter) {
        if (FsType.isAssignableFrom(Object[].class, fromType)) {
            Object[] fromArray = (Object[]) obj;
        }
        Class<?> objClass = obj.getClass();
        if (objClass.isArray()) {
            int len = Array.getLength(obj);
            Class<?> componentType = objClass.getComponentType();
            Object array = Array.newInstance(componentType, len);
            if (FsType.isAssignableFrom(Object[].class, fromType)){
                Object[] oa = (Object[]) obj;
            }
            for (int i = 0; i < len; i++) {
                Object fromValue = Array.get(obj, i);
                Object toValue = converter.convert(fromValue, componentType);
                if (toValue == NOT_SUPPORTED) {
                    return NOT_SUPPORTED;
                }
                Array.set(array, i, toValue);
            }
            return array;
        }
    }
}
