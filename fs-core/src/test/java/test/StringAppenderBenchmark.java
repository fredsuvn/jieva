package test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
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
public class StringAppenderBenchmark {

    private static final String[] strings = {
        "dfaagdgadgdgadgda",
        "gdafgggret452t442g4242tg42g",
        "saffs",
        "f32f32f32",
        "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv",
        "fsacsac",
        "dsdfsfafsfsafdf",
        "fdsafs",
    };

    /*
     * Benchmark                                   Mode  Cnt     Score      Error   Units
     * StringAppenderBenchmark.useStringAppender  thrpt    3  5495.719 ± 1909.060  ops/ms
     * StringAppenderBenchmark.useStringBuilder   thrpt    3  4733.660 ± 1952.949  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(StringAppenderBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup
    public void init() {
    }

    @Benchmark
    public void useStringAppender() {
        StringAppender stringAppender = new StringAppender();
        for (String string : strings) {
            stringAppender.append(string);
        }
        stringAppender.toString();
    }

    @Benchmark
    public void useStringBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        stringBuilder.toString();
    }
}
