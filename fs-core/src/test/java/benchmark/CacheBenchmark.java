package benchmark;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.cache.FsCache;

import java.util.concurrent.ExecutionException;
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
     * CacheBenchmark.caffeine      thrpt    3   7.018 ± 15.397  ops/ms
     * CacheBenchmark.caffeineBig   thrpt    3   6.350 ± 22.030  ops/ms
     * CacheBenchmark.caffeineSoft  thrpt    3  14.697 ± 11.022  ops/ms
     * CacheBenchmark.fsCache       thrpt    3  26.141 ± 12.452  ops/ms
     * CacheBenchmark.guava         thrpt    3   3.809 ±  0.611  ops/ms
     * CacheBenchmark.guavaBig      thrpt    3   3.241 ±  1.383  ops/ms
     * CacheBenchmark.guavaSoft     thrpt    3  13.587 ± 29.465  ops/ms
     */

    private static final Integer[] keys;
    private static final Integer[] keys2;

    static {
        keys = new Integer[500];
        keys2 = new Integer[keys.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = i;
            keys2[i] = keys.length * 2 + i;
        }
    }

    private FsCache<Integer, String> fsCache;
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
        fsCache = FsCache.softCache();
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
            fsCache.put(key, key.toString());
        }
        for (Integer key : keys) {
            fsCache.get(key);
        }
        for (Integer key : keys2) {
            fsCache.get(key, String::valueOf);
        }
    }

    @Benchmark
    public void guava() throws ExecutionException {
        for (Integer key : keys) {
            guava.put(key, key.toString());
        }
        for (Integer key : keys) {
            guava.getIfPresent(key);
        }
        for (Integer key : keys2) {
            guava.get(key, () -> String.valueOf(key));
        }
    }

    @Benchmark
    public void guavaSoft() throws ExecutionException {
        for (Integer key : keys) {
            guavaSoft.put(key, key.toString());
        }
        for (Integer key : keys) {
            guavaSoft.getIfPresent(key);
        }
        for (Integer key : keys2) {
            guavaSoft.get(key, () -> String.valueOf(key));
        }
    }

    @Benchmark
    public void guavaBig() throws ExecutionException {
        for (Integer key : keys) {
            guavaBig.put(key, key.toString());
        }
        for (Integer key : keys) {
            guavaBig.getIfPresent(key);
        }
        for (Integer key : keys2) {
            guavaBig.get(key, () -> String.valueOf(key));
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
        for (Integer key : keys2) {
            caffeine.get(key, String::valueOf);
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
        for (Integer key : keys2) {
            caffeineSoft.get(key, String::valueOf);
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
        for (Integer key : keys2) {
            caffeineBig.get(key, String::valueOf);
        }
    }
}
