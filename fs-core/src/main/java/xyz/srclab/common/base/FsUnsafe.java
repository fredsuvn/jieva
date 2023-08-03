package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.cache.FsCache;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.convert.handlers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holder for FS.
 *
 * @author fredsuvn
 */
public final class FsUnsafe {

    public static final class ForLogger {

        static final FsLogger SYSTEM_LOGGER = FsLogger.ofLevel(FsLogger.INFO_LEVEL);

        @Nullable
        public static void log(FsLogger logger, int level, Object... message) {
            if (level < logger.getLevel()) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            StackTraceElement stackTraceElement = Fs.findStackTraceCaller(
                ForLogger.class.getName(), "internalLog", 1);
            FsLogger.LogMessage log = new FsLogger.LogMessage(level, now, stackTraceElement, message);
            logger.output(log);
        }
    }

    public static final class ForCache {

        private static final Map<String, FsCache<?>> CACHE_MAP = new ConcurrentHashMap<>();
        public static final String ENUM = "ENUM";
        public static final String TYPE_PARAMETER_MAPPING = "TYPE_PARAMETER_MAPPING";
        public static final String DATE_TIME_FORMATTER = "DATE_TIME_FORMATTER";
        public static final String TYPE_WRAPPER = "TYPE_WRAPPER";

        public static void registerCache(String key, FsCache<?> cache) {
            CACHE_MAP.put(key, cache);
        }

        public static <T> FsCache<T> getOrCreateCache(String key) {
            return (FsCache<T>) CACHE_MAP.computeIfAbsent(key, it -> FsCache.newCache());
        }

        public static <T> FsCache<T> getCache(String key) {
            return (FsCache<T>) CACHE_MAP.get(key);
        }
    }

    public static final class ForBean {
        public static final FsBeanResolver DEFAULT_RESOLVER = FsBeanResolver.newBuilder().build();
    }

    public static final class ForConvert {
        public static final FsConverter DEFAULT_CONVERTER = FsConverter.withHandlers(
            new ReuseConvertHandler(),
            new BeanConvertHandler(),
            Arrays.asList(
                new DateConvertHandler(),
                new BooleanConvertHandler(),
                new NumberConvertHandler(),
                new StringConvertHandler(),
                new CollectConvertHandler()
            )
        );
        public static final FsConverter.Options DEFAULT_OPTIONS =
            FsConverter.Options.withReusePolicy(FsConverter.Options.REUSE_ASSIGNABLE);
    }
}
