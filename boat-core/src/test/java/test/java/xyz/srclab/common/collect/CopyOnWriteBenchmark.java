package test.java.xyz.srclab.common.collect;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.collect.CopyOnWriteMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
public class CopyOnWriteBenchmark {

    /*
     * Benchmark                                                            Mode  Cnt      Score      Error   Units
     * CopyOnWriteBenchmark.ReadWriteBenchmark.readWithConcurrentHashMap   thrpt    3  32904.467 ± 4379.518  ops/ms
     * CopyOnWriteBenchmark.ReadWriteBenchmark.readWithCopyOnWriteMap      thrpt    3  32712.126 ± 7546.695  ops/ms
     * CopyOnWriteBenchmark.ReadWriteBenchmark.readWithHashMap             thrpt    3  32786.971 ± 3183.833  ops/ms
     * CopyOnWriteBenchmark.ReadWriteBenchmark.writeWithConcurrentHashMap  thrpt    3   2270.768 ±  117.409  ops/ms
     * CopyOnWriteBenchmark.ReadWriteBenchmark.writeWithCopyOnWriteMap     thrpt    3    192.513 ±  466.227  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options readOptions = new OptionsBuilder().include(ReadBenchmark.class.getSimpleName()).build();
        new Runner(readOptions).run();

        Options readWriteOptions = new OptionsBuilder().include(ReadWriteBenchmark.class.getSimpleName()).build();
        new Runner(readWriteOptions).run();
    }

    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 3, time = 60)
    @Measurement(iterations = 3, time = 60)
    @Threads(1)
    @Fork(1)
    @State(value = Scope.Benchmark)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class ReadBenchmark {

        private static final List<Object> keys = new ArrayList<>();
        private static final int size = 1024 * 1024;

        private static final Map<Object, Object> hashMap = new HashMap<>();
        private static final Map<Object, Object> concurrentHashMap;
        private static final Map<Object, Object> copyOnWriteMap;

        private static int count = 0;

        static {
            for (int i = 0; i < size; i++) {
                keys.add(String.valueOf(i));
            }
            initMap(hashMap);
            concurrentHashMap = new ConcurrentHashMap<>(hashMap);
            copyOnWriteMap = new CopyOnWriteMap<>(hashMap);
        }

        private static void initMap(Map<Object, Object> map) {
            for (Object key : keys) {
                map.put(key, key);
            }
        }

        @Setup
        public void init() {
        }

        @Benchmark
        public void readWithHashMap() {
            testRead(hashMap);
        }

        @Benchmark
        public void readWithConcurrentHashMap() throws Exception {
            testRead(concurrentHashMap);
        }

        @Benchmark
        public void readWithCopyOnWriteMap() throws Exception {
            testRead(copyOnWriteMap);
        }

        private void testRead(Map<Object, Object> map) {
            int index = Math.abs(count++) % size;
            Object key = keys.get(index);
            for (int i = 0; i < 20; i++) {
                map.get(key);
            }
        }
    }

    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 3, time = 60)
    @Measurement(iterations = 3, time = 60)
    @Threads(1)
    @Fork(1)
    @State(value = Scope.Benchmark)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class ReadWriteBenchmark {

        private static final List<Object> keys = new ArrayList<>();
        private static final int size = 1024;

        private static final Map<Object, Object> hashMap = new HashMap<>();
        private static final Map<Object, Object> concurrentHashMap;
        private static final Map<Object, Object> copyOnWriteMap;

        private static int count = 0;

        static {
            for (int i = 0; i < size; i++) {
                keys.add(String.valueOf(i));
            }
            initMap(hashMap);
            concurrentHashMap = new ConcurrentHashMap<>(hashMap);
            copyOnWriteMap = new CopyOnWriteMap<>(hashMap);
        }

        private static void initMap(Map<Object, Object> map) {
            for (Object key : keys) {
                map.put(key, key);
            }
        }

        @Benchmark
        public void readWithHashMap() {
            testRead(hashMap);
        }

        @Benchmark
        public void readWithConcurrentHashMap() throws Exception {
            testRead(concurrentHashMap);
        }

        @Benchmark
        public void readWithCopyOnWriteMap() throws Exception {
            testRead(copyOnWriteMap);
        }

        @Benchmark
        public void writeWithConcurrentHashMap() throws Exception {
            testWrite(concurrentHashMap);
        }

        @Benchmark
        public void writeWithCopyOnWriteMap() throws Exception {
            testWrite(copyOnWriteMap);
        }

        private void testRead(Map<Object, Object> map) {
            int index = Math.abs(count++) % size;
            Object key = keys.get(index);
            for (int i = 0; i < 20; i++) {
                map.get(key);
            }
        }

        private void testWrite(Map<Object, Object> map) {
            int index = Math.abs(count++) % size;
            Object key = keys.get(index);
            for (int i = 0; i < 20; i++) {
                map.put(key, key);
            }
        }
    }
}
