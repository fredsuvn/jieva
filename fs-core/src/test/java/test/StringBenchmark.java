package test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.base.FsString;

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
public class StringBenchmark {

    private static final String xxx = "xxxxxxxx";
    private static final String[] xxx8 = new String[8];
    private static final String[] xxx16 = new String[16];
    private static final String[] xxx32 = new String[32];
    private static final String[] xxx64 = new String[64];
    private static final String[] xxx128 = new String[128];
    private static final String[] xxx512 = new String[512];
    private static final String[] xxx1024 = new String[1024];

    /*
     * Benchmark                              Mode  Cnt      Score       Error   Units
     * StringBenchmark.g1024                 thrpt    3    183.255 ±    55.779  ops/ms
     * StringBenchmark.g1024:useBuilder1024  thrpt    3     88.085 ±    29.839  ops/ms
     * StringBenchmark.g1024:useFs1024       thrpt    3     48.995 ±    12.777  ops/ms
     * StringBenchmark.g1024:useJoin1024     thrpt    3     46.176 ±    13.587  ops/ms
     * StringBenchmark.g128                  thrpt    3   1355.307 ±   872.516  ops/ms
     * StringBenchmark.g128:useBuilder128    thrpt    3    626.405 ±   372.354  ops/ms
     * StringBenchmark.g128:useFs128         thrpt    3    370.783 ±   313.527  ops/ms
     * StringBenchmark.g128:useJoin128       thrpt    3    358.119 ±   188.435  ops/ms
     * StringBenchmark.g16                   thrpt    3   8203.327 ± 36050.729  ops/ms
     * StringBenchmark.g16:useBuilder16      thrpt    3   3558.487 ± 14566.528  ops/ms
     * StringBenchmark.g16:useFs16           thrpt    3   2305.674 ± 10522.972  ops/ms
     * StringBenchmark.g16:useJoin16         thrpt    3   2339.166 ± 10963.148  ops/ms
     * StringBenchmark.g32                   thrpt    3   5751.406 ±   194.404  ops/ms
     * StringBenchmark.g32:useBuilder32      thrpt    3   2607.350 ±   851.603  ops/ms
     * StringBenchmark.g32:useFs32           thrpt    3   1611.399 ±   495.927  ops/ms
     * StringBenchmark.g32:useJoin32         thrpt    3   1532.657 ±   557.528  ops/ms
     * StringBenchmark.g512                  thrpt    3    365.203 ±   314.262  ops/ms
     * StringBenchmark.g512:useBuilder512    thrpt    3    169.912 ±   183.139  ops/ms
     * StringBenchmark.g512:useFs512         thrpt    3     95.612 ±    76.197  ops/ms
     * StringBenchmark.g512:useJoin512       thrpt    3     99.679 ±    55.949  ops/ms
     * StringBenchmark.g64                   thrpt    3   3057.646 ±    46.729  ops/ms
     * StringBenchmark.g64:useBuilder64      thrpt    3   1360.760 ±    95.554  ops/ms
     * StringBenchmark.g64:useFs64           thrpt    3    807.077 ±   220.475  ops/ms
     * StringBenchmark.g64:useJoin64         thrpt    3    889.809 ±   200.358  ops/ms
     * StringBenchmark.g8                    thrpt    3  15382.189 ±  6789.491  ops/ms
     * StringBenchmark.g8:useBuilder8        thrpt    3   6905.528 ±  2987.111  ops/ms
     * StringBenchmark.g8:useFs8             thrpt    3   4415.121 ±  1653.439  ops/ms
     * StringBenchmark.g8:useJoin8           thrpt    3   4061.539 ±  2525.857  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(StringBenchmark.class.getSimpleName()).build();
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
    public void useFs8() {
        FsString.concat(xxx8);
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
    public void useJoin8() {
        join(xxx8);
    }

    @Benchmark
    @Group("g16")
    public void useFs16() {
        FsString.concat(xxx16);
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
    public void useJoin16() {
        join(xxx16);
    }

    @Benchmark
    @Group("g32")
    public void useFs32() {
        FsString.concat(xxx32);
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
    public void useJoin32() {
        join(xxx32);
    }

    @Benchmark
    @Group("g64")
    public void useFs64() {
        FsString.concat(xxx64);
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
    public void useJoin64() {
        join(xxx64);
    }

    @Benchmark
    @Group("g128")
    public void useFs128() {
        FsString.concat(xxx128);
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
    public void useJoin128() {
        join(xxx128);
    }

    @Benchmark
    @Group("g512")
    public void useFs512() {
        FsString.concat(xxx512);
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
    public void useJoin512() {
        join(xxx512);
    }

    @Benchmark
    @Group("g1024")
    public void useFs1024() {
        FsString.concat(xxx1024);
        ;
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
    public void useJoin1024() {
        join(xxx1024);
    }

    private String join(String... strings) {
        return String.join("", strings);
    }
}
