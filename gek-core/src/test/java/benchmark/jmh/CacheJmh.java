package benchmark.jmh;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.openjdk.jmh.annotations.*;
import xyz.fsgek.common.cache.GekCache;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheJmh {

    private static final int LENGTH = 1000;
    private static final String[] KEYS = new String[LENGTH];

    static {
        Random random = new Random();
        for (int i = 0; i < KEYS.length; i++) {
            KEYS[i] = String.valueOf(random.nextInt());
        }
    }

    private GekCache<String, String> fsSoftCache;
    private GekCache<String, String> fsWeakCache;
    private Cache<String, String> guava;
    private Cache<String, String> guavaSoft;
    private com.github.benmanes.caffeine.cache.Cache<String, String> caffeine;
    private com.github.benmanes.caffeine.cache.Cache<String, String> caffeineSoft;
    private Map<String, String> map;

    @Setup(Level.Iteration)
    public void init() {
        fsSoftCache = GekCache.softCache();
        fsWeakCache = GekCache.weakCache();
        guava = CacheBuilder.newBuilder()
            .maximumSize(KEYS.length / 3)
            .build();
        guavaSoft = CacheBuilder.newBuilder()
            .softValues()
            .build();
        caffeine = Caffeine.newBuilder()
            .maximumSize(KEYS.length / 3)
            .build();
        caffeineSoft = Caffeine.newBuilder()
            .softValues()
            .build();
        map = new ConcurrentHashMap<>(KEYS.length / 3);
    }

    @Benchmark
    @Threads(32)
    public void map() {
        map.clear();
        for (String key : KEYS) {
            map.put(key, key + key);
        }
        for (String key : KEYS) {
            map.get(key);
        }
        for (String key : KEYS) {
            map.computeIfAbsent(key, k -> k);
        }
        for (String key : KEYS) {
            map.computeIfAbsent(key + key, k -> k);
        }
    }

    @Benchmark
    @Threads(32)
    public void fsSoft() {
        fs(fsSoftCache);
    }

    @Benchmark
    @Threads(32)
    public void fsWeak() {
        fs(fsWeakCache);
    }

    private void fs(GekCache<String, String> cache) {
        for (String key : KEYS) {
            cache.put(key, key + key);
        }
        for (String key : KEYS) {
            cache.get(key);
        }
        for (String key : KEYS) {
            cache.get(key, k -> k);
        }
        for (String key : KEYS) {
            cache.get(key + key, k -> k);
        }
    }

    @Benchmark
    @Threads(32)
    public void guava() throws ExecutionException {
        guava(guava);
    }

    @Benchmark
    @Threads(32)
    public void guavaSoft() throws ExecutionException {
        guava(guavaSoft);
    }

    private void guava(Cache<String, String> cache) throws ExecutionException {
        for (String key : KEYS) {
            cache.put(key, key + key);
        }
        for (String key : KEYS) {
            cache.getIfPresent(key);
        }
        for (String key : KEYS) {
            cache.get(key, () -> key);
        }
        for (String key : KEYS) {
            cache.get(key + key, () -> key);
        }
    }

    @Benchmark
    @Threads(32)
    public void caffeine() {
        caffeine(caffeine);
    }

    @Benchmark
    @Threads(32)
    public void caffeineSoft() {
        caffeine(caffeineSoft);
    }

    private void caffeine(com.github.benmanes.caffeine.cache.Cache<String, String> cache) {
        for (String key : KEYS) {
            cache.put(key, key + key);
        }
        for (String key : KEYS) {
            cache.getIfPresent(key);
        }
        for (String key : KEYS) {
            cache.get(key, k -> k);
        }
        for (String key : KEYS) {
            cache.get(key + key, k -> k);
        }
    }
}
