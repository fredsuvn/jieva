package internal.benchmark;

import internal.utils.DataGen;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.fs.base.chars.CharsBuilder;

import java.util.Arrays;

public class CharsBuilderJmh extends AbstractJmhBenchmark implements DataGen {

    private final char[] data = randomChars(32);
    private char[] d1;
    private String d2;
    private char[] dataSum;

    @Param({
        "5",
        "50",
        "200"
    })
    private int dataTimes;
    @Param({
        "byCharsBuilder",
        "byStringBuilder",
    })
    private String buildType;

    @Setup(Level.Trial)
    public void setup() {
        this.dataSum = new char[data.length * dataTimes];
        for (int i = 0; i < dataTimes; i++) {
            System.arraycopy(data, 0, dataSum, i * data.length, data.length);
        }
        int mid = data.length / 2;
        d1 = Arrays.copyOfRange(data, 0, mid);
        d2 = new String(Arrays.copyOfRange(data, mid, data.length));
    }

    @Benchmark
    public void buildString(Blackhole blackhole) throws Exception {
        if ("byCharsBuilder".equals(buildType)) {
            blackhole.consume(byCharsBuilder());
        } else {
            blackhole.consume(byStringBuilder());
        }
    }

    private String byCharsBuilder() throws Exception {
        CharsBuilder appender = new CharsBuilder();
        for (int i = 0; i < dataTimes; i++) {
            appender.append(d1[0]);
            appender.append(d1, 1, d1.length - 1);
            appender.append(d2);
        }
        return appender.toString();
    }

    private String byStringBuilder() throws Exception {
        StringBuilder appender = new StringBuilder();
        for (int i = 0; i < dataTimes; i++) {
            appender.append(d1[0]);
            appender.append(d1, 1, d1.length - 1);
            appender.append(d2);
        }
        return appender.toString();
    }
}
