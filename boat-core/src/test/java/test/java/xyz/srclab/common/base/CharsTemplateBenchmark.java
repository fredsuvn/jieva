package test.java.xyz.srclab.common.base;

import cn.hutool.core.util.StrUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.base.BTemplate;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
public class CharsTemplateBenchmark {

    /*
     * Benchmark                                          Mode  Cnt      Score      Error   Units
     * CharsTemplateBenchmark.withCharsTemplate          thrpt    3   4367.437 ± 2032.660  ops/ms
     * CharsTemplateBenchmark.withPreparedCharsTemplate  thrpt    3  15321.502 ± 2578.960  ops/ms
     * CharsTemplateBenchmark.withPreparedVelocity       thrpt    3    603.137 ±   42.220  ops/ms
     * CharsTemplateBenchmark.withVelocity               thrpt    3      1.825 ±    0.669  ops/ms
     *
     * 2021-10-11:
     * Benchmark                                         Mode  Cnt     Score    Error   Units
     * CharsTemplateBenchmark.useCharsTemplate          thrpt    3  1310.213 ± 83.936  ops/ms
     * CharsTemplateBenchmark.useHutool                 thrpt    3  2101.221 ± 11.355  ops/ms
     * CharsTemplateBenchmark.usePreparedCharsTemplate  thrpt    3  3382.516 ±  4.486  ops/ms
     * CharsTemplateBenchmark.usePreparedVelocity       thrpt    3   191.202 ±  2.512  ops/ms
     * CharsTemplateBenchmark.useVelocity               thrpt    3     0.553 ±  0.068  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(CharsTemplateBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    private static final String charsTemplateContent = "Hello, this is {name}, now is {date}";
    private BTemplate charsTemplate;
    private Map<Object, Object> charsTemplateArgs;

    private static final String velocityTemplate = "Hello, this is $name, now is $date";
    private VelocityEngine velocityEngine;
    private VelocityContext velocityContext;

    private static final String hutoolFormat = charsTemplateContent;
    private Map<Object, Object> hutoolFormatArgs;

    @Setup
    public void init() {
        LocalDateTime now = LocalDateTime.now();

        //CharsTemplate
        charsTemplate = BTemplate.resolve(charsTemplateContent, "{", "}");
        charsTemplateArgs = new HashMap<>();
        charsTemplateArgs.put("name", "Boat");
        charsTemplateArgs.put("date", now);

        //Velocity
        velocityEngine = new VelocityEngine();
        velocityEngine.init();
        velocityContext = new VelocityContext();
        velocityContext.put("name", "javaboy2012");
        velocityContext.put("date", now);

        //Hutool
        hutoolFormatArgs = new HashMap<>();
        hutoolFormatArgs.put("name", "Boat");
        hutoolFormatArgs.put("date", now);
    }

    @Benchmark
    public void useCharsTemplate() {
        BTemplate template = BTemplate.resolve(charsTemplateContent, "{", "}");
        StringWriter writer = new StringWriter();
        template.process(writer, charsTemplateArgs);
    }

    @Benchmark
    public void useVelocity() {
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        StringWriter writer = new StringWriter();
        ve.evaluate(velocityContext, writer, "", velocityTemplate);
    }

    @Benchmark
    public void usePreparedCharsTemplate() {
        StringWriter writer = new StringWriter();
        charsTemplate.process(writer, charsTemplateArgs);
    }

    @Benchmark
    public void usePreparedVelocity() {
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(velocityContext, writer, "", velocityTemplate);
    }

    @Benchmark
    public void useHutool() {
        StrUtil.format(hutoolFormat, hutoolFormatArgs);
    }
}
