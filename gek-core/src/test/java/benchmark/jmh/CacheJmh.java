package benchmark.jmh;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import xyz.fsgek.common.cache.GekCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheJmh {

    private static final int MAX = 1024 * 100;

    private GekCache<Integer, Integer> fsSoftCacheV1;
    private GekCache<Integer, Integer> fsWeakCacheV1;
    private GekCache<Integer, Integer> fsSoftCacheV2;
    private GekCache<Integer, Integer> fsWeakCacheV2;
    private Cache<Integer, Integer> guava;
    private Cache<Integer, Integer> guavaSoft;
    private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeine;
    private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeineSoft;
    private Map<Integer, Integer> map;

    @Setup(Level.Iteration)
    public void init() {
        System.out.println(">>>>>> init <<<<<<<<");
        fsSoftCacheV1 = GekCache.newBuilder().useSoft(true).removeListener((k, v, c) -> {
        }).v1().build();
        fsWeakCacheV1 = GekCache.newBuilder().useSoft(true).removeListener((k, v, c) -> {
        }).v1().build();
        fsSoftCacheV2 = GekCache.newBuilder().useSoft(true).removeListener((k, v, c) -> {
        }).v2().build();
        fsWeakCacheV2 = GekCache.newBuilder().useSoft(true).removeListener((k, v, c) -> {
        }).v2().build();
        guava = CacheBuilder.newBuilder()
            .removalListener(n -> {
            })
            .maximumSize(MAX / 3)
            .build();
        guavaSoft = CacheBuilder.newBuilder()
            .removalListener(n -> {
            })
            .softValues()
            .build();
        caffeine = Caffeine.newBuilder()
            .removalListener((k, v, c) -> {
            })
            .maximumSize(MAX / 3)
            .build();
        caffeineSoft = Caffeine.newBuilder()
            .removalListener((k, v, c) -> {
            })
            .softValues()
            .build();
        map = new ConcurrentHashMap<>(MAX / 3);
        System.out.println("fsSoftCacheV1: " + fsSoftCacheV1.getClass());
        System.out.println("fsWeakCacheV1: " + fsWeakCacheV1.getClass());
        System.out.println("fsSoftCacheV2: " + fsSoftCacheV2.getClass());
        System.out.println("fsWeakCacheV2: " + fsWeakCacheV2.getClass());
    }

    @Benchmark
    @Threads(64)
    public void map(Blackhole bh) {
        int value = next();
        bh.consume(map.get(value));
        map.put(value, value);
        bh.consume(map.computeIfAbsent(value + 1, k -> k));
        bh.consume(map.computeIfAbsent(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void mapFull(Blackhole bh) {
        int value = next();
        bh.consume(map.get(value));
        map.put(value, value);
        map.remove(value);
        bh.consume(map.computeIfAbsent(value, k -> k));
        bh.consume(map.computeIfAbsent(value, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsSoftV1(Blackhole bh) {
        int value = next();
        bh.consume(fsSoftCacheV1.get(value));
        fsSoftCacheV1.put(value, value);
        bh.consume(fsSoftCacheV1.get(value + 1, k -> k));
        bh.consume(fsSoftCacheV1.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsSoftV1Full(Blackhole bh) {
        int value = next();
        bh.consume(fsSoftCacheV1.get(value));
        fsSoftCacheV1.put(value, value);
        fsSoftCacheV1.remove(value);
        bh.consume(fsSoftCacheV1.get(value, k -> k));
        bh.consume(fsSoftCacheV1.get(value, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeakV1(Blackhole bh) {
        int value = next();
        bh.consume(fsWeakCacheV1.get(value));
        fsWeakCacheV1.put(value, value);
        bh.consume(fsWeakCacheV1.get(value + 1, k -> k));
        bh.consume(fsWeakCacheV1.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeakV1Full(Blackhole bh) {
        int value = next();
        bh.consume(fsWeakCacheV1.get(value));
        fsWeakCacheV1.put(value, value);
        fsWeakCacheV1.remove(value);
        bh.consume(fsWeakCacheV1.get(value, k -> k));
        bh.consume(fsWeakCacheV1.get(value, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsSoftV2(Blackhole bh) {
        int value = next();
        bh.consume(fsSoftCacheV2.get(value));
        fsSoftCacheV2.put(value, value);
        bh.consume(fsSoftCacheV2.get(value + 1, k -> k));
        bh.consume(fsSoftCacheV2.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsSoftV2Full(Blackhole bh) {
        int value = next();
        bh.consume(fsSoftCacheV2.get(value));
        fsSoftCacheV2.put(value, value);
        fsSoftCacheV2.remove(value);
        bh.consume(fsSoftCacheV2.get(value, k -> k));
        bh.consume(fsSoftCacheV2.get(value, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeakV2(Blackhole bh) {
        int value = next();
        bh.consume(fsWeakCacheV2.get(value));
        fsWeakCacheV2.put(value, value);
        bh.consume(fsWeakCacheV2.get(value + 1, k -> k));
        bh.consume(fsWeakCacheV2.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeakV2Full(Blackhole bh) {
        int value = next();
        bh.consume(fsWeakCacheV2.get(value));
        fsWeakCacheV2.put(value, value);
        fsWeakCacheV2.remove(value);
        bh.consume(fsWeakCacheV2.get(value, k -> k));
        bh.consume(fsWeakCacheV2.get(value, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void guava(Blackhole bh) throws ExecutionException {
        int value = next();
        bh.consume(guava.getIfPresent(value));
        guava.put(value, value);
        bh.consume(guava.get(value + 1, () -> value));
        bh.consume(guava.get(value + 1, () -> value));
    }

    @Benchmark
    @Threads(64)
    public void guavaFull(Blackhole bh) throws ExecutionException {
        int value = next();
        bh.consume(guava.getIfPresent(value));
        guava.put(value, value);
        guava.invalidate(value);
        bh.consume(guava.get(value, () -> value));
        bh.consume(guava.get(value, () -> value));
    }

    @Benchmark
    @Threads(64)
    public void guavaSoft(Blackhole bh) throws ExecutionException {
        int value = next();
        bh.consume(guavaSoft.getIfPresent(value));
        guavaSoft.put(value, value);
        bh.consume(guavaSoft.get(value + 1, () -> value));
        bh.consume(guavaSoft.get(value + 1, () -> value));
    }

    @Benchmark
    @Threads(64)
    public void guavaSoftFull(Blackhole bh) throws ExecutionException {
        int value = next();
        bh.consume(guavaSoft.getIfPresent(value));
        guavaSoft.put(value, value);
        guavaSoft.invalidate(value);
        bh.consume(guavaSoft.get(value, () -> value));
        bh.consume(guavaSoft.get(value, () -> value));
    }

    @Benchmark
    @Threads(64)
    public void caffeine(Blackhole bh) {
        int value = next();
        bh.consume(caffeine.getIfPresent(value));
        caffeine.put(value, value);
        bh.consume(caffeine.get(value + 1, k -> k));
        bh.consume(caffeine.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void caffeineFull(Blackhole bh) {
        int value = next();
        bh.consume(caffeine.getIfPresent(value));
        caffeine.put(value, value);
        caffeine.invalidate(value);
        bh.consume(caffeine.get(value, k -> k));
        bh.consume(caffeine.get(value, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void caffeineSoft(Blackhole bh) {
        int value = next();
        bh.consume(caffeineSoft.getIfPresent(value));
        caffeineSoft.put(value, value);
        bh.consume(caffeineSoft.get(value + 1, k -> k));
        bh.consume(caffeineSoft.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void caffeineSoftFull(Blackhole bh) {
        int value = next();
        bh.consume(caffeineSoft.getIfPresent(value));
        caffeineSoft.put(value, value);
        caffeineSoft.invalidate(value);
        bh.consume(caffeineSoft.get(value, k -> k));
        bh.consume(caffeineSoft.get(value, k -> k));
    }

    private int next() {
        return ThreadLocalRandom.current().nextInt(MAX);
    }
}
