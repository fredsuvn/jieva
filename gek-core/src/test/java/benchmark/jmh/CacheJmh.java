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
        for (String key : KEYS) {
            fsSoftCache.put(key, key + key);
        }
        for (String key : KEYS) {
            fsSoftCache.get(key);
        }
        for (String key : KEYS) {
            fsSoftCache.get(key, k -> k);
        }
        for (String key : KEYS) {
            fsSoftCache.get(key + key, k -> k);
        }
    }

    @Benchmark
    @Threads(32)
    public void fsWeak() {
        for (String key : KEYS) {
            fsWeakCache.put(key, key + key);
        }
        for (String key : KEYS) {
            fsWeakCache.get(key);
        }
        for (String key : KEYS) {
            fsWeakCache.get(key, k -> k);
        }
        for (String key : KEYS) {
            fsWeakCache.get(key + key, k -> k);
        }
    }

    @Benchmark
    @Threads(32)
    public void guava() throws ExecutionException {
        for (String key : KEYS) {
            guava.put(key, key + key);
        }
        for (String key : KEYS) {
            guava.getIfPresent(key);
        }
        for (String key : KEYS) {
            guava.get(key, () -> key);
        }
        for (String key : KEYS) {
            guava.get(key + key, () -> key);
        }
    }

    @Benchmark
    @Threads(32)
    public void guavaSoft() throws ExecutionException {
        for (String key : KEYS) {
            guavaSoft.put(key, key + key);
        }
        for (String key : KEYS) {
            guavaSoft.getIfPresent(key);
        }
        for (String key : KEYS) {
            guavaSoft.get(key, () -> key);
        }
        for (String key : KEYS) {
            guavaSoft.get(key + key, () -> key);
        }
    }

    @Benchmark
    @Threads(32)
    public void caffeine() {
        for (String key : KEYS) {
            caffeine.put(key, key + key);
        }
        for (String key : KEYS) {
            caffeine.getIfPresent(key);
        }
        for (String key : KEYS) {
            caffeine.get(key, k -> k);
        }
        for (String key : KEYS) {
            caffeine.get(key + key, k -> k);
        }
    }

    @Benchmark
    @Threads(32)
    public void caffeineSoft() {
        for (String key : KEYS) {
            caffeineSoft.put(key, key + key);
        }
        for (String key : KEYS) {
            caffeineSoft.getIfPresent(key);
        }
        for (String key : KEYS) {
            caffeineSoft.get(key, k -> k);
        }
        for (String key : KEYS) {
            caffeineSoft.get(key + key, k -> k);
        }
    }
}
