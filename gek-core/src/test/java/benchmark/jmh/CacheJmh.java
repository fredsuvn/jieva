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
        fsSoftCacheV1 = GekCache.newBuilder().useSoft(true).v1().build();
        fsWeakCacheV1 = GekCache.newBuilder().useSoft(true).v1().build();
        fsSoftCacheV2 = GekCache.newBuilder().useSoft(true).v2().build();
        fsWeakCacheV2 = GekCache.newBuilder().useSoft(true).v2().build();
        guava = CacheBuilder.newBuilder()
            .maximumSize(MAX / 3)
            .build();
        guavaSoft = CacheBuilder.newBuilder()
            .softValues()
            .build();
        caffeine = Caffeine.newBuilder()
            .maximumSize(MAX / 3)
            .build();
        caffeineSoft = Caffeine.newBuilder()
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
        bh.consume(map.put(next(), next() + next()));
        bh.consume(map.get(next()));
        bh.consume(map.computeIfAbsent(next(), k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsSoftV1(Blackhole bh) {
        fsSoftCacheV1.put(next(), next() + next());
        bh.consume(fsSoftCacheV1.get(next()));
        bh.consume(fsSoftCacheV1.get(next(), k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeakV1(Blackhole bh) {
        fsWeakCacheV1.put(next(), next() + next());
        bh.consume(fsWeakCacheV1.get(next()));
        bh.consume(fsWeakCacheV1.get(next(), k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsSoftV2(Blackhole bh) {
        fsSoftCacheV2.put(next(), next() + next());
        bh.consume(fsSoftCacheV2.get(next()));
        bh.consume(fsSoftCacheV2.get(next(), k -> k));
    }

    @Benchmark
    @Threads(64)
    public void fsWeakV2(Blackhole bh) {
        fsWeakCacheV1.put(next(), next() + next());
        bh.consume(fsWeakCacheV2.get(next()));
        bh.consume(fsWeakCacheV2.get(next(), k -> k));
    }

    @Benchmark
    @Threads(64)
    public void guava(Blackhole bh) throws ExecutionException {
        guava.put(next(), next() + next());
        bh.consume(guava.getIfPresent(next()));
        int n = next();
        bh.consume(guava.get(n, () -> n));
    }

    @Benchmark
    @Threads(64)
    public void guavaSoft(Blackhole bh) throws ExecutionException {
        guavaSoft.put(next(), next() + next());
        bh.consume(guavaSoft.getIfPresent(next()));
        int n = next();
        bh.consume(guavaSoft.get(n, () -> n));
    }

    @Benchmark
    @Threads(64)
    public void caffeine(Blackhole bh) {
        caffeine.put(next(), next() + next());
        bh.consume(caffeine.getIfPresent(next()));
        bh.consume(caffeine.get(next(), k -> k));
    }

    @Benchmark
    @Threads(64)
    public void caffeineSoft(Blackhole bh) {
        caffeineSoft.put(next(), next() + next());
        bh.consume(caffeineSoft.getIfPresent(next()));
        bh.consume(caffeineSoft.get(next(), k -> k));
    }

    private int next() {
        return ThreadLocalRandom.current().nextInt(MAX);
    }
}
