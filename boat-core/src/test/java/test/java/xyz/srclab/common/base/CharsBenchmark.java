package test.java.xyz.srclab.common.base;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.base.BRandom;
import xyz.srclab.common.base.StringAppender;

import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 60)
@Measurement(iterations = 3, time = 60)
@Threads(1)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CharsBenchmark {

    private final String shortContent = BRandom.randomString(10);
    private final String commonContent = BRandom.randomString(30);
    private final String longContent = BRandom.randomString(666);

    /*
     * Benchmark                                  Mode  Cnt     Score       Error   Units
     * CharsBenchmark.useStringAppenderAddCommon  thrpt    3    42.611 ±    51.751  ops/ms
     * CharsBenchmark.useStringAppenderAddLong    thrpt    3     4.164 ±    11.724  ops/ms
     * CharsBenchmark.useStringAppenderAddShort   thrpt    3    62.188 ±    16.736  ops/ms
     * CharsBenchmark.useStringAppenderCommon     thrpt    3  4221.374 ±  4823.314  ops/ms
     * CharsBenchmark.useStringAppenderLong       thrpt    3   443.860 ±   179.221  ops/ms
     * CharsBenchmark.useStringAppenderShort      thrpt    3  6106.644 ± 11675.240  ops/ms
     * CharsBenchmark.useStringBuilderAddCommon  thrpt    3    38.122 ±    10.316  ops/ms
     * CharsBenchmark.useStringBuilderAddLong    thrpt    3     2.682 ±     3.197  ops/ms
     * CharsBenchmark.useStringBuilderAddShort   thrpt    3    76.924 ±    44.457  ops/ms
     * CharsBenchmark.useStringBuilderCommon     thrpt    3  3043.270 ±   195.651  ops/ms
     * CharsBenchmark.useStringBuilderLong       thrpt    3   219.655 ±    27.861  ops/ms
     * CharsBenchmark.useStringBuilderShort      thrpt    3  6304.238 ±  1163.338  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(CharsBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup
    public void init() {
    }

    @Benchmark
    public void useStringAppenderShort() {
        useStringAppender0(10, shortContent);
    }

    @Benchmark
    public void useStringBuilderShort() {
        useStringBuilder0(10, shortContent);
    }

    @Benchmark
    public void useStringAppenderAddShort() {
        useStringAppender0(10000, shortContent);
    }

    @Benchmark
    public void useStringBuilderAddShort() {
        useStringBuilder0(10000, shortContent);
    }

    @Benchmark
    public void useStringAppenderCommon() {
        useStringAppender0(10, commonContent);
    }

    @Benchmark
    public void useStringBuilderCommon() {
        useStringBuilder0(10, commonContent);
    }

    @Benchmark
    public void useStringAppenderAddCommon() {
        useStringAppender0(10000, commonContent);
    }

    @Benchmark
    public void useStringBuilderAddCommon() {
        useStringBuilder0(10000, commonContent);
    }

    @Benchmark
    public void useStringAppenderLong() {
        useStringAppender0(10, longContent);
    }

    @Benchmark
    public void useStringBuilderLong() {
        useStringBuilder0(10, longContent);
    }

    @Benchmark
    public void useStringAppenderAddLong() {
        useStringAppender0(10000, longContent);
    }

    @Benchmark
    public void useStringBuilderAddLong() {
        useStringBuilder0(10000, longContent);
    }

    public void useStringAppender0(int times, String content) {
        StringAppender stringAppender = new StringAppender();
        for (int i = 0; i < times; i++) {
            stringAppender.append(content);
        }
        stringAppender.toString();
    }

    public void useStringBuilder0(int times, String content) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            stringBuilder.append(content);
        }
        stringBuilder.toString();
    }
}
