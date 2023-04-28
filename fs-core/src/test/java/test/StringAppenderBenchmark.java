package test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.base.StringAppender;

import java.util.Arrays;
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
public class StringAppenderBenchmark {

    private static final String xxx = "xxxxxxxx";
    private static final String[] xxx8 = new String[8];
    private static final String[] xxx16 = new String[16];
    private static final String[] xxx32 = new String[32];
    private static final String[] xxx64 = new String[64];
    private static final String[] xxx128 = new String[128];
    private static final String[] xxx512 = new String[512];
    private static final String[] xxx1024 = new String[1024];

    /*
     * Benchmark                                 Mode  Cnt         Score         Error  Units
     * StringAppenderBenchmark.useAppender1024  thrpt    3     40758.249 ±    6489.462  ops/s
     * StringAppenderBenchmark.useBuilder1024   thrpt    3     81516.800 ±   14015.597  ops/s
     * StringAppenderBenchmark.useConcat1024    thrpt    3     97483.950 ±  106730.613  ops/s
     * StringAppenderBenchmark.useAppender128   thrpt    3    325959.911 ±  130593.361  ops/s
     * StringAppenderBenchmark.useBuilder128    thrpt    3    652347.853 ±  111828.018  ops/s
     * StringAppenderBenchmark.useConcat128     thrpt    3    888373.228 ±  432291.998  ops/s
     * StringAppenderBenchmark.useAppender16    thrpt    3   2344641.230 ± 1775483.632  ops/s
     * StringAppenderBenchmark.useBuilder16     thrpt    3   4531363.106 ± 1082823.878  ops/s
     * StringAppenderBenchmark.useConcat16      thrpt    3   6653901.784 ± 1394553.635  ops/s
     * StringAppenderBenchmark.useAppender32    thrpt    3   1330397.959 ±   82262.207  ops/s
     * StringAppenderBenchmark.useBuilder32     thrpt    3   2296446.926 ± 1964019.224  ops/s
     * StringAppenderBenchmark.useConcat32      thrpt    3   3561250.273 ± 2531464.064  ops/s
     * StringAppenderBenchmark.useAppender512   thrpt    3     82298.862 ±    1247.358  ops/s
     * StringAppenderBenchmark.useBuilder512    thrpt    3    161401.854 ±   41708.988  ops/s
     * StringAppenderBenchmark.useConcat512     thrpt    3    208793.007 ±   53489.829  ops/s
     * StringAppenderBenchmark.useAppender64    thrpt    3    654550.404 ±  492819.065  ops/s
     * StringAppenderBenchmark.useBuilder64     thrpt    3   1035775.459 ±  742000.311  ops/s
     * StringAppenderBenchmark.useConcat64      thrpt    3   1887456.798 ± 1207836.280  ops/s
     * StringAppenderBenchmark.useAppender8     thrpt    3   4818822.342 ± 2055760.230  ops/s
     * StringAppenderBenchmark.useBuilder8      thrpt    3   7896349.818 ± 1908931.037  ops/s
     * StringAppenderBenchmark.useConcat8       thrpt    3  13171445.316 ± 3170837.318  ops/s
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(StringAppenderBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup
    public void init() {
        Arrays.fill(xxx8, xxx);
        Arrays.fill(xxx16, xxx);
        Arrays.fill(xxx32, xxx);
        Arrays.fill(xxx64, xxx);
        Arrays.fill(xxx128, xxx);
        Arrays.fill(xxx512, xxx);
        Arrays.fill(xxx1024, xxx);
    }

    @Benchmark
    @Group("g8")
    public void useAppender8() {
        StringAppender builder = new StringAppender();
        for (String s : xxx8) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g8")
    public void useBuilder8() {
        StringBuilder builder = new StringBuilder();
        for (String s : xxx8) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g8")
    public void useConcat8() {
        concat(xxx8);
    }

    @Benchmark
    @Group("g16")
    public void useAppender16() {
        StringAppender builder = new StringAppender();
        for (String s : xxx16) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g16")
    public void useBuilder16() {
        StringBuilder builder = new StringBuilder();
        for (String s : xxx16) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g16")
    public void useConcat16() {
        concat(xxx16);
    }

    @Benchmark
    @Group("g32")
    public void useAppender32() {
        StringAppender builder = new StringAppender();
        for (String s : xxx32) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g32")
    public void useBuilder32() {
        StringBuilder builder = new StringBuilder();
        for (String s : xxx32) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g32")
    public void useConcat32() {
        concat(xxx32);
    }

    @Benchmark
    @Group("g64")
    public void useAppender64() {
        StringAppender builder = new StringAppender();
        for (String s : xxx64) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g64")
    public void useBuilder64() {
        StringBuilder builder = new StringBuilder();
        for (String s : xxx64) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g64")
    public void useConcat64() {
        concat(xxx64);
    }

    @Benchmark
    @Group("g128")
    public void useAppender128() {
        StringAppender builder = new StringAppender();
        for (String s : xxx128) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g128")
    public void useBuilder128() {
        StringBuilder builder = new StringBuilder();
        for (String s : xxx128) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g128")
    public void useConcat128() {
        concat(xxx128);
    }

    @Benchmark
    @Group("g512")
    public void useAppender512() {
        StringAppender builder = new StringAppender();
        for (String s : xxx512) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g512")
    public void useBuilder512() {
        StringBuilder builder = new StringBuilder();
        for (String s : xxx512) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g512")
    public void useConcat512() {
        concat(xxx512);
    }

    @Benchmark
    @Group("g1024")
    public void useAppender1024() {
        StringAppender builder = new StringAppender();
        for (String s : xxx1024) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g1024")
    public void useBuilder1024() {
        StringBuilder builder = new StringBuilder();
        for (String s : xxx1024) {
            builder.append(s);
        }
        //builder.toString();
    }

    @Benchmark
    @Group("g1024")
    public void useConcat1024() {
        concat(xxx1024);
    }

    private String concat(String... strings) {
        int length = 0;
        for (String string : strings) {
            length += string.length();
        }
        char[] chars = new char[length];
        int off = 0;
        for (String string : strings) {
            string.getChars(0, string.length(), chars, off);
            off += string.length();
        }
        return new String(chars);
    }
}
