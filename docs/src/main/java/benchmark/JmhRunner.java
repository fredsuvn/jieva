package benchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.collect.GekArray;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

public class JmhRunner {

    public static void main(String[] args) throws Exception {
        if (GekArray.isEmpty(args)) {
            Gek.log("No JMH task.");
            return;
        }
        Gek.log("JMH tasks: ", Arrays.toString(args));
        File resultDir = Paths.get("benchmark").toFile();
        File resultFile = Paths.get("benchmark/jmhResult.txt").toFile();
        if (!resultFile.exists()) {
            resultDir.mkdirs();
            resultFile.createNewFile();
        }
        OptionsBuilder options = new OptionsBuilder();
        for (String arg : args) {
            options.include("benchmark." + GekString.capitalize(arg) + "Jmh");
        }
        options.resultFormat(ResultFormatType.TEXT);
        options.result(resultFile.getAbsolutePath());
        new Runner(options.build()).run();
    }
}
