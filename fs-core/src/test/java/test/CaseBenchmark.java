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

    private static final String TEST_CAMEL = "AaaaaaaaaBbbbbbbbbbCcccccccccccc";
    private static final String TEST_SEPARATOR = "aaaaaaaaaaa_bbbbbbbbbbbbbbbbbb_ccccccccccccc";

    private FsCase fsUpperCamel = FsCase.UPPER_CAMEL;
    private FsCase fsLowerCamel = FsCase.LOWER_CAMEL;
    private FsCase fsUnderscore = FsCase.UNDERSCORE;
    private FsCase fsHyphen = FsCase.HYPHEN;
    private CaseFormat guavaUpperCamel = CaseFormat.UPPER_CAMEL;
    private CaseFormat guavaLowerCamel = CaseFormat.LOWER_CAMEL;
    private CaseFormat guavaUnderscore = CaseFormat.UPPER_UNDERSCORE;
    private CaseFormat guavaHyphen = CaseFormat.LOWER_HYPHEN;

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(CaseBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    @Setup(Level.Iteration)
    public void init() {
    }

    @Benchmark
    public void fsCaseCamel() {
        fsUpperCamel.convert(TEST_CAMEL, fsLowerCamel);
    }

    //@Benchmark
    //public void fsCaseSeparator() {
    //    fsUnderscore.convert(TEST_SEPARATOR, fsHyphen);
    //}

    @Benchmark
    public void fsCaseMix() {
        fsUpperCamel.convert(TEST_CAMEL, fsUnderscore);
    }

    @Benchmark
    public void guavaCaseCamel() {
        guavaUpperCamel.to(guavaLowerCamel, TEST_CAMEL);
    }

    //@Benchmark
    //public void guavaCaseSeparator() {
    //    guavaUnderscore.to(guavaHyphen, TEST_SEPARATOR);
    //}

    @Benchmark
    public void guavaCaseMix() {
        guavaUpperCamel.to(guavaUnderscore, TEST_CAMEL);
    }
}
