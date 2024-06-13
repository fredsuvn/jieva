package benchmark;

import org.openjdk.jmh.annotations.*;
import xyz.fslabo.common.base.GekString;

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
        GekString.replace(shortString, "--", "++");
    }

    @Benchmark
    public void fsLongEq() {
        GekString.replace(longString, "--", "++");
    }

    @Benchmark
    public void fsShortNe() {
        GekString.replace(shortString, "--", "+++");
    }

    @Benchmark
    public void fsLongNe() {
        GekString.replace(longString, "--", "+++");
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
