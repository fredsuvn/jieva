package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.bean.FsBeanCopier;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

import static xyz.srclab.common.convert.FsConverter.Handler;

/**
 * Convert handler implementation which is used to support the conversion for bean.
 * <p>
 * This handler is system default suffix handler (with {@link #BeanConvertHandler()}),
 * any object will be seen as "bean", and the conversion means create new object and copy properties.
 * <p>
 * It creates new {@link Map} for these target map types:
 * <ul>
 *     <li>{@link Map};</li>
 *     <li>{@link AbstractMap};</li>
 *     <li>{@link LinkedHashMap};</li>
 *     <li>{@link HashMap};</li>
 *     <li>{@link TreeMap};</li>
 *     <li>{@link ConcurrentMap};</li>
 *     <li>{@link ConcurrentHashMap};</li>
 *     <li>{@link Hashtable};</li>
 *     <li>{@link ConcurrentSkipListMap};</li>
 * </ul>
 * For other types, it creates with their empty constructor.
 * <p>
 * Note if the {@code obj} is null, return null.
 *
 * @author fredsuvn
 */
public class BeanConvertHandler implements Handler {

    private static final Map<Class<?>, Supplier<Object>> GENERATOR_MAP = new ConcurrentHashMap<>();

    static {
        GENERATOR_MAP.put(Map.class, LinkedHashMap::new);
        GENERATOR_MAP.put(AbstractMap.class, LinkedHashMap::new);
        GENERATOR_MAP.put(LinkedHashMap.class, LinkedHashMap::new);
        GENERATOR_MAP.put(HashMap.class, HashMap::new);
        GENERATOR_MAP.put(TreeMap.class, TreeMap::new);
        GENERATOR_MAP.put(ConcurrentMap.class, ConcurrentHashMap::new);
        GENERATOR_MAP.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
        GENERATOR_MAP.put(Hashtable.class, Hashtable::new);
        GENERATOR_MAP.put(ConcurrentSkipListMap.class, ConcurrentSkipListMap::new);
    }

    private final FsBeanCopier beanCopier;

    /**
     * Constructs with {@link FsBeanCopier#defaultCopier()}.
     *
     * @see #BeanConvertHandler(FsBeanCopier)
     */
    public BeanConvertHandler() {
        this(FsBeanCopier.defaultCopier());
    }

    /**
     * Constructs with given bean copier.
     *
     * @param beanCopier given bean copier
     */
    public BeanConvertHandler(FsBeanCopier beanCopier) {
        this.beanCopier = beanCopier;
    }

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options, FsConverter converter) {
        if (source == null) {
            return null;
        }
        Class<?> rawType = FsType.getRawType(targetType);
        if (rawType == null) {
            return Fs.CONTINUE;
        }
        Supplier<Object> generator = GENERATOR_MAP.get(rawType);
        Object dest;
        if (generator != null) {
            dest = generator.get();
        } else {
            dest = FsType.newInstance(rawType);
        }
        return beanCopier.copyProperties(source, sourceType, dest, targetType, converter);
    }
}
