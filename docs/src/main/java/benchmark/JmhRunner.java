package benchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.GekString;
import xyz.fslabo.common.collect.GekArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class JmhRunner {

    public static void main(String[] args) throws Exception {
        runBenchmark(args);
    }

    private static void runBenchmark(String[] args) throws Exception {
        if (GekArray.isEmpty(args)) {
            Jie.log("No JMH task.");
            return;
        }
        Jie.log("JMH tasks: ", Arrays.toString(args));
        Path resultDir = Paths.get("benchmark");
        File resultDirFile = resultDir.toFile();
        resultDirFile.mkdirs();
        for (String arg : args) {
            runBenchmark(resultDir, arg);
        }
    }

    private static void runBenchmark(Path resultDir, String taskName) throws IOException, RunnerException {
        Path resultPath = resultDir.resolve(taskName + "-jmh.txt");
        File resultFile = resultPath.toFile();
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }
        new Runner(
            new OptionsBuilder()
                .include("benchmark." + GekString.capitalize(taskName) + "Jmh")
                .resultFormat(ResultFormatType.TEXT)
                .result(resultFile.getAbsolutePath())
                .build()
        ).run();
    }
}
