package benchmark;

import com.google.common.base.CaseFormat;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import xyz.fslabo.common.base.GekCase;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 3, time = 3)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CaseJmh {

    private static final String TEST_CAMEL = "getSimpleNameOptionsBuilder";
    private static final String TEST_UNDERSCORE = "get_Simple_Name_Options_Builder";
    private static final String TEST_HYPHEN = "get-Simple-Name-Options-Builder";

    private GekCase fsUpperCamel = GekCase.UPPER_CAMEL;
    private GekCase fsLowerCamel = GekCase.LOWER_CAMEL;
    private GekCase fsUpperUnderscore = GekCase.UPPER_UNDERSCORE;
    private GekCase fsLowerUnderscore = GekCase.LOWER_UNDERSCORE;
    private GekCase fsHyphen = GekCase.HYPHEN;
    private GekCase fsUnderscore = GekCase.UNDERSCORE;
    private CaseFormat guavaUpperCamel = CaseFormat.UPPER_CAMEL;
    private CaseFormat guavaLowerCamel = CaseFormat.LOWER_CAMEL;
    private CaseFormat guavaUpperUnderscore = CaseFormat.UPPER_UNDERSCORE;
    private CaseFormat guavaLowerUnderscore = CaseFormat.LOWER_UNDERSCORE;
    private CaseFormat guavaLowerHyphen = CaseFormat.LOWER_HYPHEN;

    @Setup(Level.Iteration)
    public void init() {
    }

    @Benchmark
    public void fsCamel(Blackhole bh) {
        bh.consume(fsUpperCamel.toCase(TEST_CAMEL, fsLowerCamel));
        bh.consume(fsLowerCamel.toCase(TEST_CAMEL, fsUpperCamel));
    }

    @Benchmark
    public void fsUnderscore(Blackhole bh) {
        bh.consume(fsUpperUnderscore.toCase(TEST_UNDERSCORE, fsLowerUnderscore));
        bh.consume(fsLowerUnderscore.toCase(TEST_UNDERSCORE, fsUpperUnderscore));
    }

    @Benchmark
    public void fsMix(Blackhole bh) {
        bh.consume(fsUpperCamel.toCase(TEST_CAMEL, fsLowerUnderscore));
        bh.consume(fsUpperUnderscore.toCase(TEST_UNDERSCORE, fsLowerCamel));
    }

    @Benchmark
    public void fsMix2(Blackhole bh) {
        bh.consume(fsHyphen.toCase(TEST_HYPHEN, fsUnderscore));
        bh.consume(fsUnderscore.toCase(TEST_UNDERSCORE, fsHyphen));
    }

    @Benchmark
    public void guavaCamel(Blackhole bh) {
        bh.consume(guavaUpperCamel.to(guavaLowerCamel, TEST_CAMEL));
        bh.consume(guavaLowerCamel.to(guavaUpperCamel, TEST_CAMEL));
    }

    @Benchmark
    public void guavaUnderscore(Blackhole bh) {
        bh.consume(guavaUpperUnderscore.to(guavaLowerUnderscore, TEST_UNDERSCORE));
        bh.consume(guavaLowerUnderscore.to(guavaUpperUnderscore, TEST_UNDERSCORE));
    }

    @Benchmark
    public void guavaMix(Blackhole bh) {
        bh.consume(guavaUpperCamel.to(guavaLowerUnderscore, TEST_CAMEL));
        bh.consume(guavaUpperUnderscore.to(guavaLowerCamel, TEST_UNDERSCORE));
    }

    @Benchmark
    public void guavaMix2(Blackhole bh) {
        bh.consume(guavaLowerHyphen.to(guavaLowerUnderscore, TEST_HYPHEN));
        bh.consume(guavaLowerUnderscore.to(guavaLowerHyphen, TEST_UNDERSCORE));
    }
}
