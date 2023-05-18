package test;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.cache.FsCache;

import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
//@Threads(7)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheBenchmark {

    /*
     * Benchmark                     Mode  Cnt   Score    Error   Units
     * CacheBenchmark.caffeine      thrpt    3   5.894 ± 13.217  ops/ms
     * CacheBenchmark.caffeineBig   thrpt    3   9.383 ±  3.068  ops/ms
     * CacheBenchmark.caffeineSoft  thrpt    3  10.356 ±  5.225  ops/ms
     * CacheBenchmark.fsCache       thrpt    3  18.500 ±  1.771  ops/ms
     * CacheBenchmark.guava         thrpt    3   5.284 ±  1.654  ops/ms
     * CacheBenchmark.guavaBig      thrpt    3   4.607 ±  3.366  ops/ms
     * CacheBenchmark.guavaSoft     thrpt    3   9.927 ±  2.187  ops/ms
     */

    private static final Integer[] keys;

    static {
        keys = new Integer[1000];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = i;
        }
    }

    private FsCache<String> fsCache;
    private Cache<Integer, String> guava;
    private Cache<Integer, String> guavaSoft;
    private Cache<Integer, String> guavaBig;
    private com.github.benmanes.caffeine.cache.Cache<Integer, String> caffeine;
    private com.github.benmanes.caffeine.cache.Cache<Integer, String> caffeineSoft;
    private com.github.benmanes.caffeine.cache.Cache<Integer, String> caffeineBig;

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(CacheBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup(Level.Iteration)
    public void init() {
        fsCache = FsCache.newCache();
        guava = CacheBuilder.newBuilder()
            .maximumSize(keys.length / 10)
            .build();
        guavaSoft = CacheBuilder.newBuilder()
            .softValues()
            .build();
        guavaBig = CacheBuilder.newBuilder()
            .maximumSize(keys.length)
            .build();
        caffeine = Caffeine.newBuilder()
            .maximumSize(keys.length / 10)
            .build();
        caffeineSoft = Caffeine.newBuilder()
            .softValues()
            .build();
        caffeineBig = Caffeine.newBuilder()
            .maximumSize(keys.length)
            .build();
    }

    @Benchmark
    public void fsCache() {
        for (Integer key : keys) {
            fsCache.set(key, key.toString());
        }
        for (Integer key : keys) {
            fsCache.get(key);
        }
    }

    @Benchmark
    public void guava() {
        for (Integer key : keys) {
            guava.put(key, key.toString());
        }
        for (Integer key : keys) {
            guava.getIfPresent(key);
        }
    }

    @Benchmark
    public void guavaSoft() {
        for (Integer key : keys) {
            guavaSoft.put(key, key.toString());
        }
        for (Integer key : keys) {
            guavaSoft.getIfPresent(key);
        }
    }

    @Benchmark
    public void guavaBig() {
        for (Integer key : keys) {
            guavaBig.put(key, key.toString());
        }
        for (Integer key : keys) {
            guavaBig.getIfPresent(key);
        }
    }

    @Benchmark
    public void caffeine() {
        for (Integer key : keys) {
            caffeine.put(key, key.toString());
        }
        for (Integer key : keys) {
            caffeine.getIfPresent(key);
        }
    }

    @Benchmark
    public void caffeineSoft() {
        for (Integer key : keys) {
            caffeineSoft.put(key, key.toString());
        }
        for (Integer key : keys) {
            caffeineSoft.getIfPresent(key);
        }
    }

    @Benchmark
    public void caffeineBig() {
        for (Integer key : keys) {
            caffeineBig.put(key, key.toString());
        }
        for (Integer key : keys) {
            caffeineBig.getIfPresent(key);
        }
    }
}
