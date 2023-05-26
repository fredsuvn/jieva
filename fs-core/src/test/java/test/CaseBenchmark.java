package test;

import com.google.common.base.CaseFormat;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.base.FsCase;

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
public class CaseBenchmark {

    private static final String TEST_CAMEL = "getSimpleNameOptionsBuilder";
    private static final String TEST_SEPARATOR = "get_Simple_Name_Options_Builder";

    private FsCase fsUpperCamel = FsCase.UPPER_CAMEL;
    private FsCase fsLowerCamel = FsCase.LOWER_CAMEL;
    private FsCase fsUpperUnderscore = FsCase.UPPER_UNDERSCORE;
    private FsCase fsLowerUnderscore = FsCase.LOWER_UNDERSCORE;
    private CaseFormat guavaUpperCamel = CaseFormat.UPPER_CAMEL;
    private CaseFormat guavaLowerCamel = CaseFormat.LOWER_CAMEL;
    private CaseFormat guavaUpperUnderscore = CaseFormat.UPPER_UNDERSCORE;
    private CaseFormat guavaLowerUnderscore = CaseFormat.LOWER_UNDERSCORE;

    /*
     * Benchmark                       Mode  Cnt      Score     Error   Units
     * CaseBenchmark.fsCamel          thrpt    3   1402.223 ±  26.896  ops/ms
     * CaseBenchmark.fsMix            thrpt    3   1251.326 ±  19.838  ops/ms
     * CaseBenchmark.fsUnderscore     thrpt    3   1127.971 ±  27.798  ops/ms
     * CaseBenchmark.guavaCamel       thrpt    3   2139.035 ±  65.923  ops/ms
     * CaseBenchmark.guavaMix         thrpt    3   2017.303 ±   9.888  ops/ms
     * CaseBenchmark.guavaUnderscore  thrpt    3  19476.851 ± 961.036  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(CaseBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup(Level.Iteration)
    public void init() {
    }

    @Benchmark
    public void fsCamel() {
        fsUpperCamel.convert(TEST_CAMEL, fsLowerCamel);
        fsLowerCamel.convert(TEST_CAMEL, fsUpperCamel);
    }

    @Benchmark
    public void fsUnderscore() {
        fsUpperUnderscore.convert(TEST_SEPARATOR, fsLowerUnderscore);
        fsLowerUnderscore.convert(TEST_SEPARATOR, fsUpperUnderscore);
    }

    @Benchmark
    public void fsMix() {
        fsUpperCamel.convert(TEST_CAMEL, fsLowerUnderscore);
        fsUpperUnderscore.convert(TEST_SEPARATOR, fsLowerCamel);
    }

    @Benchmark
    public void guavaCamel() {
        guavaUpperCamel.to(guavaLowerCamel, TEST_CAMEL);
        guavaLowerCamel.to(guavaUpperCamel, TEST_CAMEL);
    }

    @Benchmark
    public void guavaUnderscore() {
        guavaUpperUnderscore.to(guavaLowerUnderscore, TEST_SEPARATOR);
        guavaLowerUnderscore.to(guavaUpperUnderscore, TEST_SEPARATOR);
    }

    @Benchmark
    public void guavaMix() {
        guavaUpperCamel.to(guavaLowerUnderscore, TEST_CAMEL);
        guavaUpperUnderscore.to(guavaLowerCamel, TEST_SEPARATOR);
    }
}
