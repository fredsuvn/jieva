package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.bean.FsBean;
import xyz.srclab.common.bean.FsBeanCopier;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntFunction;

import static xyz.srclab.common.convert.FsConverter.Handler;

/**
 * Convert handler implementation which is used to support the conversion for bean.
 * <p>
 * This handler is system default suffix handler (with {@link #BeanConvertHandler()}),
 * any object will be seen as "bean", and the conversion means create new object and copy properties.
 * <p>
 * Note if the {@code obj} is null, return {@link Fs#CONTINUE}.
 *
 * @author fredsuvn
 */
public class BeanConvertHandler implements Handler {

    private static final Map<Class<?>, Generator> GENERATOR_MAP = new ConcurrentHashMap<>();

    static {
        GENERATOR_MAP.put(Map.class, new Generator(true, LinkedHashMap::new));
        GENERATOR_MAP.put(LinkedHashMap.class, new Generator(true, LinkedHashMap::new));
        GENERATOR_MAP.put(HashMap.class, new Generator(true, HashMap::new));
        GENERATOR_MAP.put(TreeMap.class, new Generator(false, size->new TreeMap<>()));
        GENERATOR_MAP.put(ConcurrentHashMap.class, new Generator(true, ConcurrentHashMap::new));
    }

    private final FsBeanCopier beanCopier;

    /**
     * Constructs with bean copier: {@link FsBeanCopier#defaultCopier()}.
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
            return Fs.CONTINUE;
        }
        Class<?> rawType = FsType.getRawType(targetType);
        if (rawType == null) {
            return Fs.CONTINUE;
        }
        Generator generator = GENERATOR_MAP.get(rawType);
        Object dest;
        if (generator != null) {
            if (generator.needSize) {
                // FsBean bean =
            }
        }
        // Object result = copier.copyProperties(source, targetType);
        // if (result == null) {
        //     return CONTINUE;
        // }
        // return result;
        return Fs.CONTINUE;
    }

    private static final class Generator {

        private final boolean needSize;
        private final IntFunction<Object> generator;

        private Generator(boolean needSize, IntFunction<Object> generator) {
            this.needSize = needSize;
            this.generator = generator;
        }

        public boolean needSize() {
            return needSize;
        }

        public Object generate(int size) {
            return generator.apply(size);
        }
    }
}
