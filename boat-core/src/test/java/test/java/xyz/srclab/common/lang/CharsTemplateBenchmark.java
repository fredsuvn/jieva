package test.java.xyz.srclab.common.lang;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.lang.CharsTemplate;
import xyz.srclab.common.test.TestLogger;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
public class CharsTemplateBenchmark {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private static final String charsTemplateContent = "Hello, this is {name}, now is {date}";
    private CharsTemplate charsTemplate;
    private Map<Object, Object> charsTemplateArgs;

    private static final String velocityTemplate = "Hello, this is $name, now is $date";
    private VelocityEngine velocityEngine;
    private VelocityContext velocityContext;

    @Setup
    public void init() {
        LocalDateTime now = LocalDateTime.now();

        //CharsTemplate
        charsTemplate = CharsTemplate.resolve(charsTemplateContent, "{", "}");
        charsTemplateArgs = new HashMap<>();
        charsTemplateArgs.put("name", "Boat");
        charsTemplateArgs.put("date", now);

        //Velocity
        velocityEngine = new VelocityEngine();
        velocityEngine.init();
        velocityContext = new VelocityContext();
        velocityContext.put("name", "javaboy2012");
        velocityContext.put("date", now);
    }

    @Benchmark
    public void withCharsTemplate() {
        CharsTemplate template = CharsTemplate.resolve(charsTemplateContent, "{", "}");
        StringWriter writer = new StringWriter();
        template.process(writer, charsTemplateArgs);
    }

    @Benchmark
    public void withVelocity() {
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        StringWriter writer = new StringWriter();
        ve.evaluate(velocityContext, writer, "", velocityTemplate);
    }

    @Benchmark
    public void withPreparedCharsTemplate() {
        StringWriter writer = new StringWriter();
        charsTemplate.process(writer, charsTemplateArgs);
    }

    @Benchmark
    public void withPreparedVelocity() {
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(velocityContext, writer, "", velocityTemplate);
    }

    /*
     * Benchmark                                          Mode  Cnt      Score      Error   Units
     * CharsTemplateBenchmark.withCharsTemplate          thrpt    3   4367.437 ± 2032.660  ops/ms
     * CharsTemplateBenchmark.withPreparedCharsTemplate  thrpt    3  15321.502 ± 2578.960  ops/ms
     * CharsTemplateBenchmark.withPreparedVelocity       thrpt    3    603.137 ±   42.220  ops/ms
     * CharsTemplateBenchmark.withVelocity               thrpt    3      1.825 ±    0.669  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(CharsTemplateBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }
}
