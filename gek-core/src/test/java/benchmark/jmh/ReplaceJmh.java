package benchmark.jmh;

import org.openjdk.jmh.annotations.*;
import xyz.fsgek.common.base.FsString;

import java.util.concurrent.TimeUnit;

/**
 * @author fredsuvn
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ReplaceJmh {

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
