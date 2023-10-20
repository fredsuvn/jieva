package benchmark.jmh;

import com.google.common.base.CaseFormat;
import org.openjdk.jmh.annotations.*;
import xyz.fsgek.common.base.FsCase;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CaseJmh {

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
