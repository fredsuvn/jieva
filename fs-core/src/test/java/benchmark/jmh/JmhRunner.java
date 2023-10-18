package benchmark.jmh;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class JmhRunner {

    private static final String OUTPUT_DIR = "fs-core/src/test/java/jmh/";

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
            .include("jmh.benchmarks.*Jmh")
            .resultFormat(ResultFormatType.TEXT)
            .result(OUTPUT_DIR + "result.text")
            .build();
        new Runner(options).run();
    }
}
