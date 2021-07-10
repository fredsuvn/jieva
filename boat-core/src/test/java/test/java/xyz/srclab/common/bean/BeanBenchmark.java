package test.java.xyz.srclab.common.bean;

import org.apache.commons.beanutils.BeanUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.bean.Beans;
import xyz.srclab.common.test.TestLogger;

import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 3, time = 3)
@Threads(16)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BeanBenchmark {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private BenchmarkBean initBean;

    @Setup
    public void init() {
        initBean = new BenchmarkBean();
        initBean.setS1("s1");
        initBean.setS2("s2");
        initBean.setS3("s3");
        initBean.setS4("s4");
        initBean.setS5("s5");
        initBean.setS6("s6");
        initBean.setS7("s7");
        initBean.setS8("s8");
        initBean.setI1(1);
        initBean.setI2(2);
        initBean.setI3(3);
        initBean.setI4(4);
        initBean.setI5(5);
        initBean.setI6(6);
        initBean.setI7(7);
        initBean.setI8(8);
    }

    @Benchmark
    public void withBeans() {
        Beans.copyProperties(initBean, new BenchmarkBean());
    }

    @Benchmark
    public void withBeanUtils() throws Exception {
        BeanUtils.copyProperties(new BenchmarkBean(), initBean);
    }

    @Benchmark
    public void withSetDirectly() throws Exception {
        BenchmarkBean bean = new BenchmarkBean();
        bean.setS1(initBean.getS1());
        bean.setS2(initBean.getS2());
        bean.setS3(initBean.getS3());
        bean.setS4(initBean.getS4());
        bean.setS5(initBean.getS5());
        bean.setS6(initBean.getS6());
        bean.setS7(initBean.getS7());
        bean.setS8(initBean.getS8());
        bean.setI1(initBean.getI1());
        bean.setI2(initBean.getI2());
        bean.setI3(initBean.getI3());
        bean.setI4(initBean.getI4());
        bean.setI5(initBean.getI5());
        bean.setI6(initBean.getI6());
        bean.setI7(initBean.getI7());
        bean.setI8(initBean.getI8());
    }

    /*
     * Benchmark                       Mode  Cnt       Score       Error   Units
     * BeanBenchmark.withBeanUtils    thrpt    3     366.501 ±   266.764  ops/ms
     * BeanBenchmark.withBeans        thrpt    3    7508.884 ±  5385.327  ops/ms
     * BeanBenchmark.withSetDirectly  thrpt    3  152606.863 ± 32203.076  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(BeanBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }
}
