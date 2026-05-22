package internal.benchmark;

import internal.utils.DataGen;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.base.random.Rog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CharsBuilderJmh extends AbstractJmhBenchmark implements DataGen {

    private List<Object> data;

    @Param({
        "8",
        "32",
        "64",
        "1024"
    })
    private int dataLength;
    @Param({
        "32",
        "128",
        "1024"
    })
    private int maxBlockSize;
    @Param({
        "byCharsBuilder",
        "byStringBuilder",
    })
    private String buildType;

    @Setup(Level.Trial)
    public void setup() {
        this.data = new ArrayList<>(dataLength);
        Random random = new Random();
        Rog<Object> rog = Rog.newBuilder()
            .weight(50, () -> randomChars(random.nextInt(maxBlockSize) + 2))
            .weight(50, () -> new String(randomChars(random.nextInt(maxBlockSize) + 2)))
            .build();
        for (int i = 0; i < dataLength; i++) {
            data.add(rog.next());
        }
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
        for (Object datum : data) {
            if (datum instanceof char[]) {
                char[] chars = (char[]) datum;
                appender.append(chars[0]);
                appender.append(chars, 1, chars.length - 1);
                continue;
            }
            appender.append((String) datum);
        }
        return appender.toString();
    }

    private String byStringBuilder() throws Exception {
        StringBuilder appender = new StringBuilder();
        for (Object datum : data) {
            if (datum instanceof char[]) {
                char[] chars = (char[]) datum;
                appender.append(chars);
                continue;
            }
            appender.append((String) datum);
        }
        return appender.toString();
    }
}
