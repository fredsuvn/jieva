package xyz.fsgik.common.convert.handlers;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.base.FsArray;
import xyz.fsgik.common.base.obj.FsObj;
import xyz.fsgik.common.base.obj.ParameterizedObj;
import xyz.fsgik.common.collect.FsCollect;
import xyz.fsgik.common.convert.FsConverter;
import xyz.fsgik.common.reflect.FsReflect;
import xyz.fsgik.common.reflect.FsType;

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
 * It supports target types in:
 * <ul>
 *     <li>Array;</li>
 *     <li>{@link Iterable};</li>
 *     <li>{@link Collection};</li>
 *     <li>{@link List};</li>
 *     <li>{@link AbstractList};</li>
 *     <li>{@link ArrayList};</li>
 *     <li>{@link LinkedList};</li>
 *     <li>{@link CopyOnWriteArrayList};</li>
 *     <li>{@link Set};</li>
 *     <li>{@link LinkedHashSet};</li>
 *     <li>{@link HashSet};</li>
 *     <li>{@link TreeSet};</li>
 *     <li>{@link ConcurrentSkipListSet};</li>
 * </ul>
 * Note if the {@code obj} is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class CollectConvertHandler implements FsConverter.Handler {

    /**
     * An instance.
     */
    public static final CollectConvertHandler INSTANCE = new CollectConvertHandler();

    private static final Map<Class<?>, Generator> GENERATOR_MAP = new ConcurrentHashMap<>();

    static {
        GENERATOR_MAP.put(Iterable.class, new Generator(true, ArrayList::new));
        GENERATOR_MAP.put(Collection.class, new Generator(true, LinkedHashSet::new));
        GENERATOR_MAP.put(List.class, new Generator(true, ArrayList::new));
        GENERATOR_MAP.put(AbstractList.class, new Generator(true, ArrayList::new));
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
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        if (source == null) {
            return Fs.CONTINUE;
        }
        if (targetType instanceof Class) {
            if (((Class<?>) targetType).isArray()) {
                ParameterizedObj<Iterable<?>> sourceInfo = toGenericInfo(source, sourceType);
                if (sourceInfo == null) {
                    return Fs.CONTINUE;
                }
                return convertArray(sourceInfo, ((Class<?>) targetType).getComponentType(), converter);
            }
            return convertIterableType(source, sourceType, targetType, converter);
        } else if (targetType instanceof GenericArrayType) {
            ParameterizedObj<Iterable<?>> sourceInfo = toGenericInfo(source, sourceType);
            if (sourceInfo == null) {
                return Fs.CONTINUE;
            }
            return convertArray(sourceInfo, ((GenericArrayType) targetType).getGenericComponentType(), converter);
        }
        return convertIterableType(source, sourceType, targetType, converter);
    }

    @Nullable
    private Object convertIterableType(
        @Nullable Object obj, Type sourceType, Type targetType, FsConverter converter) {
        ParameterizedType targetItType = FsReflect.getGenericSuperType(targetType, Iterable.class);
        if (targetItType == null) {
            return Fs.CONTINUE;
        }
        ParameterizedObj<Iterable<?>> sourceInfo = toGenericInfo(obj, sourceType);
        if (sourceInfo == null) {
            return Fs.CONTINUE;
        }
        if (FsReflect.getRawType(sourceInfo.getType()).isArray()) {
            return convertArray(sourceInfo, targetItType.getActualTypeArguments()[0], converter);
        }
        Generator generator = GENERATOR_MAP.get(FsReflect.getRawType(targetItType));
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
                converter
            );
        } else {
            return convertCollection(
                sourceInfo.getObject(),
                generator.generate(0),
                sourceInfo.getActualTypeArgument(0),
                targetItType.getActualTypeArguments()[0],
                converter
            );
        }
    }

    @Nullable
    private Object convertCollection(
        Iterable<?> source,
        Collection<Object> dest,
        Type sourceComponentType,
        Type destComponentType,
        FsConverter converter
    ) {
        for (Object srcValue : source) {
            Object targetValue = converter.convertObject(srcValue, sourceComponentType, destComponentType);
            if (targetValue == Fs.RETURN) {
                return Fs.BREAK;
            }
            dest.add(targetValue);
        }
        return dest;
    }

    @Nullable
    private Object convertArray(
        ParameterizedObj<Iterable<?>> sourceInfo,
        Type targetComponentType,
        FsConverter converter
    ) {
        Collection<?> srcList;
        if (sourceInfo.getObject() instanceof Collection) {
            srcList = (Collection<?>) sourceInfo.getObject();
        } else {
            srcList = FsCollect.toCollection(new LinkedList<>(), sourceInfo.getObject());
        }
        Class<?> targetArrayClass = FsReflect.arrayClass(targetComponentType);
        Object targetArray = FsArray.newArray(targetArrayClass.getComponentType(), srcList.size());
        int i = 0;
        for (Object srcValue : srcList) {
            Object targetValue = converter.convertObject(
                srcValue, sourceInfo.getActualTypeArgument(0), targetComponentType);
            if (targetValue == Fs.RETURN) {
                return Fs.BREAK;
            }
            Array.set(targetArray, i, targetValue);
            i++;
        }
        return targetArray;
    }

    @Nullable
    private ParameterizedObj<Iterable<?>> toGenericInfo(Object obj, Type type) {
        if (type instanceof Class && ((Class<?>) type).isArray()) {
            Iterable<?> it = asIterable(obj);
            if (it == null) {
                return null;
            }
            return Fs.as(
                FsObj.wrap(
                    it,
                    FsType.parameterizedType(it.getClass(), Collections.singletonList(((Class<?>) type).getComponentType()))
                ).toParameterizedObj()
            );
        }
        if (type instanceof GenericArrayType) {
            Iterable<?> it = asIterable(obj);
            if (it == null) {
                return null;
            }
            return Fs.as(
                FsObj.wrap(
                    it,
                    FsType.parameterizedType(it.getClass(), Collections.singletonList(((GenericArrayType) type).getGenericComponentType()))
                ).toParameterizedObj()
            );
        }
        if (!(obj instanceof Iterable)) {
            return null;
        }
        ParameterizedType itType = FsReflect.getGenericSuperType(type, Iterable.class);
        if (itType == null) {
            return null;
        }
        Iterable<?> it = asIterable(obj);
        if (it == null) {
            return null;
        }
        return Fs.as(
            FsObj.wrap(it, itType).toParameterizedObj()
        );
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
