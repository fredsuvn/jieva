//package xyz.srclab.common.convert.handlers;
//
//import xyz.srclab.annotations.Nullable;
//import xyz.srclab.common.base.FsArray;
//import xyz.srclab.common.convert.FsConvertHandler;
//import xyz.srclab.common.convert.FsConverter;
//import xyz.srclab.common.reflect.FsType;
//
//import java.lang.reflect.Array;
//import java.lang.reflect.GenericArrayType;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.*;
//
///**
// * Convert handler implementation supports converting collections.
// * It supports target type in:
// * <ul>
// *     <li>{@link Iterable}</li>
// * </ul>
// * Note if the {@code obj} is null, return {@link #NOT_SUPPORTED}.
// *
// * @author fredsuvn
// */
//public class CollectConvertHandler implements FsConvertHandler {
//
//    @Override
//    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
//        if (obj == null) {
//            return NOT_SUPPORTED;
//        }
//        if (targetType instanceof Class) {
//            return convertClass(obj, fromType, (Class<?>) targetType, converter);
//        }
//        if (targetType instanceof ParameterizedType) {
//            return convertParameterized(obj, fromType, (ParameterizedType) targetType, converter);
//        }
//        if (targetType instanceof GenericArrayType) {
//            return convertGenericArray(obj, fromType, (GenericArrayType) targetType, converter);
//        }
//        return NOT_SUPPORTED;
//    }
//
//    @Nullable
//    private Object convertClass(Object obj, Type fromType, Class<?> targetType, FsConverter converter) {
//        if (targetType.isArray()) {
//            if (fromType instanceof Class) {
//                if (((Class<?>) fromType).isArray()) {
//                    Class<?> targetComponentType = targetType.getComponentType();
//                    return convertArray(arrayAsList(obj), ((Class<?>) fromType).getComponentType(), targetComponentType, converter);
//                } else {
//                    ParameterizedType itType = FsType.getGenericSuperType(fromType, Iterable.class);
//                    if (itType == null) {
//                        return NOT_SUPPORTED;
//                    }
////                    Type fromComponentType = itType.get
//                    return convertArray(obj, )
//                }
//            } else if (fromType instanceof GenericArrayType) {
//                Class<?> targetComponentType = targetType.getComponentType();
//                return convertArray(arrayAsList(obj), ((GenericArrayType) fromType).getGenericComponentType(), targetComponentType, converter);
//            } else {
//                return NOT_SUPPORTED;
//            }
//        } else if (Objects.equals(targetType, List.class)
//            || Objects.equals(targetType, ArrayList.class)
//            || Objects.equals(targetType, Collection.class)
//            || Objects.equals(targetType, Iterable.class)) {
//            if (obj instanceof Collection) {
//                Collection<?> collection = (Collection<?>) obj;
//                return new ArrayList<>(collection);
//            }
//            if (obj instanceof Iterable) {
//                Iterable<?> it = (Iterable<?>) obj;
//                LinkedList<? super Object> list = new LinkedList<>();
//                for (Object o : it) {
//                    list.add(o);
//                }
//                return new ArrayList<>(list);
//            }
//        } else if (Objects.equals(targetType, LinkedList.class)) {
//            if (obj instanceof Collection) {
//                Collection<?> collection = (Collection<?>) obj;
//                return new LinkedList<>(collection);
//            }
//            if (obj instanceof Iterable) {
//                Iterable<?> it = (Iterable<?>) obj;
//                LinkedList<? super Object> list = new LinkedList<>();
//                for (Object o : it) {
//                    list.add(o);
//                }
//                return list;
//            }
//        } else if (Objects.equals(targetType, Set.class)
//            || Objects.equals(targetType, LinkedHashSet.class)) {
//            if (obj instanceof Collection) {
//                Collection<?> collection = (Collection<?>) obj;
//                return new LinkedHashSet<>(collection);
//            }
//            if (obj instanceof Iterable) {
//                Iterable<?> it = (Iterable<?>) obj;
//                LinkedHashSet<? super Object> set = new LinkedHashSet<>();
//                for (Object o : it) {
//                    set.add(o);
//                }
//                return set;
//            }
//        } else if (Objects.equals(targetType, HashSet.class)) {
//            if (obj instanceof Collection) {
//                Collection<?> collection = (Collection<?>) obj;
//                return new HashSet<>(collection);
//            }
//            if (obj instanceof Iterable) {
//                Iterable<?> it = (Iterable<?>) obj;
//                HashSet<? super Object> set = new HashSet<>();
//                for (Object o : it) {
//                    set.add(o);
//                }
//                return set;
//            }
//        } else if (Objects.equals(targetType, TreeSet.class)) {
//            if (obj instanceof Collection) {
//                Collection<?> collection = (Collection<?>) obj;
//                return new TreeSet<>(collection);
//            }
//            if (obj instanceof Iterable) {
//                Iterable<?> it = (Iterable<?>) obj;
//                TreeSet<? super Object> set = new TreeSet<>();
//                for (Object o : it) {
//                    set.add(o);
//                }
//                return set;
//            }
//        }
//        return NOT_SUPPORTED;
//    }
//
//    @Nullable
//    private Object convertCollection(Object obj, Type fromType, Type targetType, FsConverter converter) {
//        return NOT_SUPPORTED;
//    }
//
//    @Nullable
//    private Object convertArray(List<?> srcList, Type fromComponentType, Type targetComponentType, FsConverter converter) {
//        if (srcList == null) {
//            return NOT_SUPPORTED;
//        }
//        Class<?> targetArrayClass = FsType.arrayClass(targetComponentType);
//        Object targetArray = FsArray.newArray(targetArrayClass.getComponentType(), srcList.size());
//        for (int i = 0; i < srcList.size(); i++) {
//            Object srcValue = srcList.get(i);
//            Object targetValue = converter.convert(srcValue, fromComponentType, targetComponentType);
//            if (targetValue == NOT_SUPPORTED) {
//                return NOT_SUPPORTED;
//            }
//            Array.set(targetArray, i, targetValue);
//        }
//        return targetArray;
//    }
//
//    @Nullable
//    private List<?> arrayAsList(Object array) {
//        if (array instanceof Object[]) {
//            return FsArray.asList((Object[]) array);
//        }
//        if (array instanceof boolean[]) {
//            return FsArray.asList((boolean[]) array);
//        }
//        if (array instanceof byte[]) {
//            return FsArray.asList((byte[]) array);
//        }
//        if (array instanceof short[]) {
//            return FsArray.asList((short[]) array);
//        }
//        if (array instanceof char[]) {
//            return FsArray.asList((char[]) array);
//        }
//        if (array instanceof int[]) {
//            return FsArray.asList((int[]) array);
//        }
//        if (array instanceof long[]) {
//            return FsArray.asList((long[]) array);
//        }
//        if (array instanceof float[]) {
//            return FsArray.asList((float[]) array);
//        }
//        if (array instanceof double[]) {
//            return FsArray.asList((double[]) array);
//        }
//        return null;
//    }
//}
