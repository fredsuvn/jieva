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
     * This readonly concurrent map stores all cache for fs.
     * If you need to do something for the cache (such as call the {@link FsCache#cleanUp()}), find it by its name.
     * The name usually write on the relevant document (such as class doc).
     */
    public static final Map<String, FsCache<?>> FS_CACHE_MAP;

    static {
        Map<String, FsCache<?>> content = new ConcurrentHashMap<>();
        content.put("FsEnum", FsCache.newCache());
        FS_CACHE_MAP = Collections.unmodifiableMap(content);
    }
}
