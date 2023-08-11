package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.base.FsObj;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntFunction;

/**
 * Convert handler implementation which is used to support the conversion of collection types.
 * It supports target type in:
 * <ul>
 *     <li>{@link Iterable}</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class CollectConvertHandler implements FsConverter.Handler {

    private static final Map<Class<?>, Generator> GENERATOR_MAP = new ConcurrentHashMap<>();

    static {
        GENERATOR_MAP.put(Iterable.class, new Generator(true, ArrayList::new));
        GENERATOR_MAP.put(Collection.class, new Generator(true, LinkedHashSet::new));
        GENERATOR_MAP.put(List.class, new Generator(true, ArrayList::new));
        GENERATOR_MAP.put(ArrayList.class, new Generator(true, ArrayList::new));
        GENERATOR_MAP.put(LinkedList.class, new Generator(false, size -> new LinkedList<>()));
        GENERATOR_MAP.put(CopyOnWriteArrayList.class, new Generator(false, size -> new CopyOnWriteArrayList<>()));
        GENERATOR_MAP.put(Set.class, new Generator(true, LinkedHashSet::new));
        GENERATOR_MAP.put(LinkedHashSet.class, new Generator(true, LinkedHashSet::new));
        GENERATOR_MAP.put(HashSet.class, new Generator(false, size -> new HashSet<>()));
        GENERATOR_MAP.put(TreeSet.class, new Generator(false, size -> new TreeSet<>()));
        GENERATOR_MAP.put(ConcurrentSkipListSet.class, new Generator(false, size -> new ConcurrentSkipListSet<>()));
    }

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options, FsConverter converter) {
        if (source == null) {
            return Fs.CONTINUE;
        }
        if (targetType instanceof Class) {
            if (((Class<?>) targetType).isArray()) {
                FsObj<Iterable<?>> sourceInfo = toGenericInfo(source, sourceType);
                if (sourceInfo == null) {
                    return Fs.CONTINUE;
                }
                return convertArray(sourceInfo, ((Class<?>) targetType).getComponentType(), options, converter);
            }
            return convertIterableType(source, sourceType, targetType, options, converter);
        } else if (targetType instanceof GenericArrayType) {
            FsObj<Iterable<?>> sourceInfo = toGenericInfo(source, sourceType);
            if (sourceInfo == null) {
                return Fs.CONTINUE;
            }
            return convertArray(sourceInfo, ((GenericArrayType) targetType).getGenericComponentType(), options, converter);
        }
        return convertIterableType(source, sourceType, targetType, options, converter);
    }

    @Nullable
    private Object convertIterableType(
        @Nullable Object obj, Type fromType, Type targetType, FsConverter.Options options, FsConverter converter) {
        ParameterizedType targetItType = FsType.getGenericSuperType(targetType, Iterable.class);
        if (targetItType == null) {
            return Fs.CONTINUE;
        }
        FsObj<Iterable<?>> sourceInfo = toGenericInfo(obj, fromType);
        if (sourceInfo == null) {
            return Fs.CONTINUE;
        }
        if (sourceInfo.getRawType().isArray()) {
            return convertArray(sourceInfo, targetItType.getActualTypeArguments()[0], options, converter);
        }
        Generator generator = GENERATOR_MAP.get(sourceInfo.getRawType());
        if (generator == null) {
            return Fs.CONTINUE;
        }
        if (generator.needSize()) {
            Collection<?> srcList = FsCollect.asOrToList(sourceInfo.getObject());
            return convertCollection(
                srcList,
                generator.generate(srcList.size()),
                sourceInfo.getActualTypeArgument(0),
                targetItType.getActualTypeArguments()[0],
                options,
                converter
            );
        } else {
            return convertCollection(
                sourceInfo.getObject(),
                generator.generate(0),
                sourceInfo.getActualTypeArgument(0),
                targetItType.getActualTypeArguments()[0],
                options,
                converter
            );
        }
    }

    @Nullable
    private Object convertCollection(
        Iterable<?> src,
        Collection<Object> dest,
        Type fromComponentType,
        Type targetComponentType,
        FsConverter.Options options,
        FsConverter converter
    ) {
        for (Object srcValue : src) {
            Object targetValue = converter.convertObject(srcValue, fromComponentType, targetComponentType, options);
            if (targetValue == Fs.RETURN) {
                return Fs.BREAK;
            }
            dest.add(targetValue);
        }
        return dest;
    }

    @Nullable
    private Object convertArray(
        FsObj<Iterable<?>> sourceInfo,
        Type targetComponentType,
        FsConverter.Options options,
        FsConverter converter
    ) {
        Collection<?> srcList;
        if (sourceInfo.getObject() instanceof Collection) {
            srcList = (Collection<?>) sourceInfo.getObject();
        } else {
            srcList = FsCollect.toCollection(new LinkedList<>(), sourceInfo.getObject());
        }
        Class<?> targetArrayClass = FsType.arrayClass(targetComponentType);
        Object targetArray = FsArray.newArray(targetArrayClass.getComponentType(), srcList.size());
        int i = 0;
        for (Object srcValue : srcList) {
            Object targetValue = converter.convertObject(
                srcValue, sourceInfo.getActualTypeArgument(0), targetComponentType, options);
            if (targetValue == Fs.RETURN) {
                return Fs.BREAK;
            }
            Array.set(targetArray, i, targetValue);
            i++;
        }
        return targetArray;
    }

    @Nullable
    private FsObj<Iterable<?>> toGenericInfo(Object obj, Type type) {
        if (type instanceof Class && ((Class<?>) type).isArray()) {
            Iterable<?> it = asIterable(obj);
            if (it == null) {
                return null;
            }
            return FsObj.wrap(
                it,
                FsType.parameterizedType(it.getClass(), Collections.singletonList(((Class<?>) type).getComponentType()))
            );
        }
        if (type instanceof GenericArrayType) {
            Iterable<?> it = asIterable(obj);
            if (it == null) {
                return null;
            }
            return FsObj.wrap(
                it,
                FsType.parameterizedType(it.getClass(), Collections.singletonList(((GenericArrayType) type).getGenericComponentType()))
            );
        }
        if (!(obj instanceof Iterable)) {
            return null;
        }
        ParameterizedType itType = FsType.getGenericSuperType(type, Iterable.class);
        if (itType == null) {
            return null;
        }
        Iterable<?> it = asIterable(obj);
        if (it == null) {
            return null;
        }
        return FsObj.wrap(it, itType);
    }

    @Nullable
    private Iterable<?> asIterable(Object obj) {
        if (obj instanceof Iterable) {
            return (Iterable<?>) obj;
        }
        if (obj instanceof Object[]) {
            return FsArray.asList((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return FsArray.asList((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return FsArray.asList((byte[]) obj);
        }
        if (obj instanceof short[]) {
            return FsArray.asList((short[]) obj);
        }
        if (obj instanceof char[]) {
            return FsArray.asList((char[]) obj);
        }
        if (obj instanceof int[]) {
            return FsArray.asList((int[]) obj);
        }
        if (obj instanceof long[]) {
            return FsArray.asList((long[]) obj);
        }
        if (obj instanceof float[]) {
            return FsArray.asList((float[]) obj);
        }
        if (obj instanceof double[]) {
            return FsArray.asList((double[]) obj);
        }
        return null;
    }

    private static final class Generator {

        private final boolean needSize;
        private final IntFunction<Collection<Object>> generator;

        private Generator(boolean needSize, IntFunction<Collection<Object>> generator) {
            this.needSize = needSize;
            this.generator = generator;
        }

        public boolean needSize() {
            return needSize;
        }

        public Collection<Object> generate(int size) {
            return generator.apply(size);
        }
    }
}
