package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.cache.FsCache;

import java.time.LocalDateTime;
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
}
