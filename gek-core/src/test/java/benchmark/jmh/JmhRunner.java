package benchmark.jmh;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.nio.file.Paths;

public class JmhRunner {

    public static void main(String[] args) throws Exception {
        File resultDir = Paths.get("docs/benchmark").toFile();
        File resultFile = Paths.get("docs/benchmark/jmhResult.txt").toFile();
        if (!resultFile.exists()) {
            resultDir.mkdirs();
            resultFile.createNewFile();
        }
        Options options = new OptionsBuilder()
            .include("benchmark.jmh.*Jmh")
            .resultFormat(ResultFormatType.TEXT)
            .result(resultFile.getAbsolutePath())
            .build();
        new Runner(options).run();
    }
}
