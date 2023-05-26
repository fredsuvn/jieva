package test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.base.FsString;

import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 3, time = 5)
@Threads(7)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ReplaceBenchmark {

    private static final String shortString = "123--123--123--123";
    private static final String longString = "123--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123" +
        "--123--123--123";

    /*
     * Benchmark                     Mode  Cnt      Score      Error   Units
     * ReplaceBenchmark.fsLongEq    thrpt    3   7304.054 ±  569.326  ops/ms
     * ReplaceBenchmark.fsLongNe    thrpt    3   4103.141 ±  198.919  ops/ms
     * ReplaceBenchmark.fsShortEq   thrpt    3  86722.616 ± 2890.738  ops/ms
     * ReplaceBenchmark.fsShortNe   thrpt    3  63329.312 ± 8297.470  ops/ms
     * ReplaceBenchmark.jdkLongEq   thrpt    3   2727.665 ± 1912.250  ops/ms
     * ReplaceBenchmark.jdkLongNe   thrpt    3   2327.668 ±  209.087  ops/ms
     * ReplaceBenchmark.jdkShortEq  thrpt    3  14529.210 ±  468.592  ops/ms
     * ReplaceBenchmark.jdkShortNe  thrpt    3  14423.217 ±  319.071  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(ReplaceBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup
    public void init() {
    }

    @Benchmark
    public void fsShortEq() {
        FsString.replace(shortString, "--", "++");
    }

    @Benchmark
    public void fsLongEq() {
        FsString.replace(longString, "--", "++");
    }

    @Benchmark
    public void fsShortNe() {
        FsString.replace(shortString, "--", "+++");
    }

    @Benchmark
    public void fsLongNe() {
        FsString.replace(longString, "--", "+++");
    }

    @Benchmark
    public void jdkShortEq() {
        shortString.replaceAll("--", "++");
    }

    @Benchmark
    public void jdkLongEq() {
        longString.replaceAll("--", "++");
    }

    @Benchmark
    public void jdkShortNe() {
        shortString.replaceAll("--", "+++");
    }

    @Benchmark
    public void jdkLongNe() {
        longString.replaceAll("--", "+++");
    }
}
