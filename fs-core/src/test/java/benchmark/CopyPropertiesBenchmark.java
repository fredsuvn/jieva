package benchmark;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.base.Fs;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fredsuvn
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
@Threads(1)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CopyPropertiesBenchmark {

    private static final Bean bean = new Bean();

    /*
     * Benchmark                            Mode  Cnt     Score    Error   Units
     * CopyPropertiesBenchmark.apacheCopy  thrpt    3   600.970 ± 20.965  ops/ms
     * CopyPropertiesBenchmark.fsCopy      thrpt    3  3205.447 ± 88.343  ops/ms
     * CopyPropertiesBenchmark.hutoolCopy  thrpt    3   563.066 ± 22.820  ops/ms
     * CopyPropertiesBenchmark.springCopy  thrpt    3   276.925 ±  2.676  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
            .include(CopyPropertiesBenchmark.class.getSimpleName())
            .output(BenchmarkUtils.OUTPUT_DIR + "CopyProperties.txt")
            .build();
        new Runner(options).run();
    }

    @Setup
    public void init() {
    }

    @Benchmark
    public void springCopy() {
        org.springframework.beans.BeanUtils.copyProperties(bean, new Bean(), "s1", "i1", "l1");
    }

    @Benchmark
    public void hutoolCopy() {
        BeanUtil.copyProperties(bean, new Bean(), "s1", "i1", "l1");
    }

    @Benchmark
    public void apacheCopy() throws InvocationTargetException, IllegalAccessException {
        BeanUtils.copyProperties(new Bean(), bean);
    }

    @Benchmark
    public void fsCopy() {
        Fs.copyProperties(bean, new Bean(), "s1", "i1", "l1");
    }

    @Data
    public static class Bean {
        private String s1 = "s1";
        private String s2 = "s2";
        private String s3 = "s3";
        private String s4 = "s4";
        private int i1 = 1;
        private int i2 = 2;
        private int i3 = 3;
        private int i4 = 4;
        private List<String> l1 = Arrays.asList("1", "2");
        private List<String> l2 = Arrays.asList("1", "2");
        private List<String> l3 = Arrays.asList("1", "2");
        private List<String> l4 = Arrays.asList("1", "2");
    }
}
