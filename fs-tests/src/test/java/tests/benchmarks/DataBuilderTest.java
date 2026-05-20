package tests.benchmarks;

import internal.utils.DataGen;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.base.random.Rog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataBuilderTest implements DataGen {

    // @Test
    // public void testBytesAppender() throws Exception {
    //     byte[] data = randomBytes(100);
    //     byte[] dataTimes = new byte[data.length * DATA_TIMES];
    //     for (int i = 0; i < DATA_TIMES; i++) {
    //         System.arraycopy(data, 0, dataTimes, i * data.length, data.length);
    //     }
    //     testBytesAppender(data, dataTimes);
    //     testByteArrayOutputStream(data, dataTimes);
    //     testBytesBuilder(data, dataTimes);
    // }
    //
    // private void testBytesAppender(byte[] data, byte[] dataTimes) throws Exception {
    //     int mid = data.length / 2;
    //     byte[] d1 = Arrays.copyOfRange(data, 0, mid);
    //     byte[] d2 = Arrays.copyOfRange(data, mid, data.length);
    //     BytesAppender appender = new BytesAppender();
    //     for (int i = 0; i < DATA_TIMES; i++) {
    //         appender.write(d1[0]);
    //         appender.write(d1, 1, d1.length - 1);
    //         appender.write(d2);
    //     }
    //     assertArrayEquals(dataTimes, appender.toByteArray());
    // }
    //
    // private void testBytesBuilder(byte[] data, byte[] dataTimes) throws Exception {
    //     int mid = data.length / 2;
    //     byte[] d1 = Arrays.copyOfRange(data, 0, mid);
    //     byte[] d2 = Arrays.copyOfRange(data, mid, data.length);
    //     BytesBuilder appender = new BytesBuilder();
    //     for (int i = 0; i < DATA_TIMES; i++) {
    //         appender.write(d1[0]);
    //         appender.write(d1, 1, d1.length - 1);
    //         appender.write(d2);
    //     }
    //     assertArrayEquals(dataTimes, appender.toByteArray());
    // }
    //
    // private void testByteArrayOutputStream(byte[] data, byte[] dataTimes) throws Exception {
    //     int mid = data.length / 2;
    //     byte[] d1 = Arrays.copyOfRange(data, 0, mid);
    //     byte[] d2 = Arrays.copyOfRange(data, mid, data.length);
    //     ByteArrayOutputStream appender = new ByteArrayOutputStream();
    //     for (int i = 0; i < DATA_TIMES; i++) {
    //         appender.write(d1[0]);
    //         appender.write(d1, 1, d1.length - 1);
    //         appender.write(d2);
    //     }
    //     assertArrayEquals(dataTimes, appender.toByteArray());
    // }

    @Test
    public void testBuildForString() throws Exception {
        testBuildForString(8);
        testBuildForString(32);
        testBuildForString(128);
        testBuildForString(1024);
    }

    private void testBuildForString(int dataLength) throws Exception {
        List<Object> data = new ArrayList<>(dataLength);
        Random random = new Random();
        Rog<Object> rog = Rog.newBuilder()
            .weight(50, () -> randomChars(random.nextInt(1024) + 2))
            .weight(50, () -> new String(randomChars(random.nextInt(1024) + 2)))
            .build();
        for (int i = 0; i < dataLength; i++) {
            data.add(rog.next());
        }
        String result = data.stream().map(e -> {
            if (e instanceof char[]) {
                return new String((char[]) e);
            }
            return e.toString();
        }).collect(Collectors.joining(""));
        assertEquals(result, byCharsBuilder(data));
        assertEquals(result, byStringBuilder(data));
    }

    private String byCharsBuilder(List<Object> data) throws Exception {
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

    private String byStringBuilder(List<Object> data) throws Exception {
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
