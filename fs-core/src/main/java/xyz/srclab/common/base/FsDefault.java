package xyz.srclab.common.base;

import xyz.srclab.common.cache.FsCache;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default configuration and global setting for fs.
 *
 * @author fredsuvn
 */
public class FsDefault {


    /**
     * Default charset: {@link StandardCharsets#UTF_8}.
     */
    public static Charset charset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * Returns default cache of cache name.
     * <p>
     * If you need to do something for default cache of fs (such as call the {@link FsCache#cleanUp()}),
     * find it by its name.
     * All the name in {@link Cache}.
     *
     * @param name cache name
     * @see Cache
     */
    public static <T> FsCache<T> getCache(String name) {
        FsCache<T> result = (FsCache<T>) Cache.FS_CACHE_MAP.get(name);
        if (result == null) {
            throw new IllegalArgumentException("Cannot find fs default cache for name: " + name);
        }
        return result;
    }

    /**
     * Default cache infos: caches map, names, and etc.
     */
    public static final class Cache {

        /**
         * This readonly concurrent map stores all cache for fs.
         * <p>
         * If you need to do something for default cache of fs (such as call the {@link FsCache#cleanUp()}),
         * find it by its name.
         * All the name in {@link Cache}.
         */
        public static final Map<String, FsCache<?>> FS_CACHE_MAP;

        /**
         * Name of cache for enum.
         */
        public static final String ENUM_CACHE_NAME = "fs-enum";

        static {
            Map<String, FsCache<?>> content = new ConcurrentHashMap<>();
            content.put(Cache.ENUM_CACHE_NAME, FsCache.newCache());
            FS_CACHE_MAP = Collections.unmodifiableMap(content);
        }
    }
}
