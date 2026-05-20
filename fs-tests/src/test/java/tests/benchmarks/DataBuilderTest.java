package tests.benchmarks;

import internal.utils.DataGen;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.bytes.BytesBuilder;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.base.random.Rog;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataBuilderTest implements DataGen {

    @Test
    public void testBuildForBytes() throws Exception {
        testBuildForBytes(8, 128);
        testBuildForBytes(32, 128);
        testBuildForBytes(128, 128);
        testBuildForBytes(1024, 128);
        testBuildForBytes(8, 1024);
        testBuildForBytes(32, 1024);
        testBuildForBytes(128, 1024);
        testBuildForBytes(1024, 1024);
    }

    private void testBuildForBytes(int dataLength, int maxBlockSize) throws Exception {
        List<byte[]> data = new ArrayList<>(dataLength);
        Random random = new Random();
        for (int i = 0; i < dataLength; i++) {
            byte[] bytes = new byte[random.nextInt(maxBlockSize) + 2];
            random.nextBytes(bytes);
            data.add(bytes);
        }
        byte[] expected = new byte[data.stream().mapToInt(b -> b.length).sum() * 2];
        int off = 0;
        for (byte[] bytes : data) {
            System.arraycopy(bytes, 0, expected, off, bytes.length);
            off += bytes.length;
            System.arraycopy(bytes, 0, expected, off, bytes.length);
            off += bytes.length;
        }
        assertArrayEquals(expected, byBytesBuilder(data));
        assertArrayEquals(expected, byByteArrayOutputStream(data));
    }

    private byte[] byBytesBuilder(List<byte[]> data) throws Exception {
        BytesBuilder appender = new BytesBuilder();
        for (byte[] bytes : data) {
            appender.append(bytes[0]);
            appender.append(bytes, 1, bytes.length - 1);
            appender.append(bytes);
        }
        return appender.toByteArray();
    }

    private byte[] byByteArrayOutputStream(List<byte[]> data) throws Exception {
        ByteArrayOutputStream appender = new ByteArrayOutputStream();
        for (byte[] bytes : data) {
            appender.write(bytes[0]);
            appender.write(bytes, 1, bytes.length - 1);
            appender.write(bytes);
        }
        return appender.toByteArray();
    }

    @Test
    public void testBuildForString() throws Exception {
        testBuildForString(8, 128);
        testBuildForString(32, 128);
        testBuildForString(128, 128);
        testBuildForString(1024, 128);
        testBuildForString(8, 1024);
        testBuildForString(32, 1024);
        testBuildForString(128, 1024);
        testBuildForString(1024, 1024);
    }

    private void testBuildForString(int dataLength, int maxBlockSize) throws Exception {
        List<Object> data = new ArrayList<>(dataLength);
        Random random = new Random();
        Rog<Object> rog = Rog.newBuilder()
            .weight(50, () -> randomChars(random.nextInt(maxBlockSize) + 2))
            .weight(50, () -> new String(randomChars(random.nextInt(maxBlockSize) + 2)))
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
