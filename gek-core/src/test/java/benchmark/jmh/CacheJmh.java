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

    private GekCache<Integer, Integer> fsSoft;
    private GekCache<Integer, Integer> fsWeak;
    private Cache<Integer, Integer> guava;
    private Cache<Integer, Integer> guavaSoft;
    private Cache<Integer, Integer> guavaWeak;
    private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeine;
    private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeineSoft;
    private com.github.benmanes.caffeine.cache.Cache<Integer, Integer> caffeineWeak;
    private Map<Integer, Integer> map;

    @Setup(Level.Iteration)
    public void init() {
        System.out.println(">>>>>> init <<<<<<<<");
        fsSoft = GekCache.newBuilder()
            .useSoft(true)
            // .removeListener((k, v, c) -> {
            // })
            .build();
        fsWeak = GekCache.newBuilder()
            .useSoft(false)
            // .removeListener((k, v, c) -> {
            // })
            .build();
        guava = CacheBuilder.newBuilder()
            // .removalListener(n -> {
            // })
            .maximumSize(MAX / 3)
            .build();
        guavaSoft = CacheBuilder.newBuilder()
            // .removalListener(n -> {
            // })
            .softValues()
            .build();
        guavaWeak = CacheBuilder.newBuilder()
            // .removalListener(n -> {
            // })
            .weakValues()
            .build();
        caffeine = Caffeine.newBuilder()
            // .removalListener((k, v, c) -> {
            // })
            .maximumSize(MAX / 3)
            .build();
        caffeineSoft = Caffeine.newBuilder()
            // .removalListener((k, v, c) -> {
            // })
            .softValues()
            .build();
        caffeineWeak = Caffeine.newBuilder()
            // .removalListener((k, v, c) -> {
            // })
            .weakValues()
            .build();
        map = new ConcurrentHashMap<>(MAX / 3);
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
    public void fsSoft(Blackhole bh) {
        int value = next();
        bh.consume(fsSoft.get(value));
        fsSoft.put(value, value);
        bh.consume(fsSoft.get(value + 1, k -> k));
        bh.consume(fsSoft.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsSoftFull(Blackhole bh) {
        int value = next();
        bh.consume(fsSoft.get(value));
        fsSoft.put(value, value);
        fsSoft.remove(value);
        bh.consume(fsSoft.get(value, k -> k));
        bh.consume(fsSoft.get(value, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeak(Blackhole bh) {
        int value = next();
        bh.consume(fsWeak.get(value));
        fsWeak.put(value, value);
        bh.consume(fsWeak.get(value + 1, k -> k));
        bh.consume(fsWeak.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeakFull(Blackhole bh) {
        int value = next();
        bh.consume(fsWeak.get(value));
        fsWeak.put(value, value);
        fsWeak.remove(value);
        bh.consume(fsWeak.get(value, k -> k));
        bh.consume(fsWeak.get(value, k -> k));
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
    public void guavaWeak(Blackhole bh) throws ExecutionException {
        int value = next();
        bh.consume(guavaWeak.getIfPresent(value));
        guavaWeak.put(value, value);
        bh.consume(guavaWeak.get(value + 1, () -> value));
        bh.consume(guavaWeak.get(value + 1, () -> value));
    }

    @Benchmark
    @Threads(64)
    public void guavaWeakFull(Blackhole bh) throws ExecutionException {
        int value = next();
        bh.consume(guavaWeak.getIfPresent(value));
        guavaWeak.put(value, value);
        guavaWeak.invalidate(value);
        bh.consume(guavaWeak.get(value, () -> value));
        bh.consume(guavaWeak.get(value, () -> value));
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

    @Benchmark
    @Threads(64)
    public void caffeineWeak(Blackhole bh) {
        int value = next();
        bh.consume(caffeineWeak.getIfPresent(value));
        caffeineWeak.put(value, value);
        bh.consume(caffeineWeak.get(value + 1, k -> k));
        bh.consume(caffeineWeak.get(value + 1, k -> k));
    }

    @Benchmark
    @Threads(64)
    public void caffeineWeakFull(Blackhole bh) {
        int value = next();
        bh.consume(caffeineWeak.getIfPresent(value));
        caffeineWeak.put(value, value);
        caffeineWeak.invalidate(value);
        bh.consume(caffeineWeak.get(value, k -> k));
        bh.consume(caffeineWeak.get(value, k -> k));
    }

    private int next() {
        return ThreadLocalRandom.current().nextInt(MAX);
    }
}
