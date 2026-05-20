package internal.benchmark;

import internal.utils.DataGen;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;
import space.sunqian.fs.base.bytes.BytesBuilder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BytesBuilderJmh extends AbstractJmhBenchmark implements DataGen {

    private List<byte[]> data;

    @Param({
        "8",
        "32",
        "1024"
    })
    private int dataLength;
    @Param({
        "128",
        "1024"
    })
    private int maxBlockSize;
    @Param({
        "byBytesBuilder",
        "byByteArrayOutputStream",
    })
    private String buildType;

    @Setup(Level.Trial)
    public void setup() {
        this.data = new ArrayList<>(dataLength);
        Random random = new Random();
        for (int i = 0; i < dataLength; i++) {
            byte[] bytes = new byte[random.nextInt(maxBlockSize) + 2];
            random.nextBytes(bytes);
            data.add(bytes);
        }
    }

    @Benchmark
    public void buildBytes(Blackhole blackhole) throws Exception {
        if ("byBytesBuilder".equals(buildType)) {
            blackhole.consume(byBytesBuilder());
        } else {
            blackhole.consume(byByteArrayOutputStream());
        }
    }

    private byte[] byBytesBuilder() throws Exception {
        BytesBuilder appender = new BytesBuilder();
        for (byte[] bytes : data) {
            appender.append(bytes[0]);
            appender.append(bytes, 1, bytes.length - 1);
            appender.append(bytes);
        }
        return appender.toByteArray();
    }

    private byte[] byByteArrayOutputStream() throws Exception {
        ByteArrayOutputStream appender = new ByteArrayOutputStream();
        for (byte[] bytes : data) {
            appender.write(bytes[0]);
            appender.write(bytes, 1, bytes.length - 1);
            appender.write(bytes);
        }
        return appender.toByteArray();
    }
}