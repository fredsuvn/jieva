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
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
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
     * Benchmark                                  Mode  Cnt       Score        Error   Units
     * StringAppenderBenchmark.useAppender       thrpt    3    6146.967 ±    797.707  ops/ms
     * StringAppenderBenchmark.useAppenderShort  thrpt    3    6569.592 ±   1059.797  ops/ms
     * StringAppenderBenchmark.useBuilder        thrpt    3    4824.778 ±   1399.142  ops/ms
     * StringAppenderBenchmark.useBuilderShort   thrpt    3  179211.338 ± 179601.239  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(StringAppenderBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup
    public void init() {
    }

    @Benchmark
    public void useAppender() {
        StringAppender stringAppender = new StringAppender();
        for (String string : strings) {
            stringAppender.append(string);
        }
        stringAppender.toString();
    }

    @Benchmark
    public void useBuilder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        stringBuilder.toString();
    }

    @Benchmark
    public void useAppenderShort() {
        StringAppender appender = new StringAppender();
        String word = appender
            .append('h')
            .append('e')
            .append("llo")
            .append(' ')
            .append('w')
            .append("orld")
            .append('!')
            .toString();
    }

    @Benchmark
    public void useBuilderShort() {
        StringBuilder builder = new StringBuilder();
        String word = builder
            .append('h')
            .append('e')
            .append("llo")
            .append(' ')
            .append('w')
            .append("orld")
            .append('!')
            .toString();
    }
}
