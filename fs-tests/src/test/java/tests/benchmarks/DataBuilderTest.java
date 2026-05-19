package tests.benchmarks;

import internal.utils.DataGen;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.chars.CharsBuilder;

import java.util.Arrays;

public class DataBuilderTest implements DataGen {

    private static final int DATA_TIMES = 15;

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
        char[] data = randomChars(32);
        char[] dataSum = new char[data.length * DATA_TIMES];
        for (int i = 0; i < DATA_TIMES; i++) {
            System.arraycopy(data, 0, dataSum, i * data.length, data.length);
        }
        int mid = data.length / 2;
        char[] d1 = Arrays.copyOfRange(data, 0, mid);
        String d2 = new String(Arrays.copyOfRange(data, mid, data.length));
    }

    private String byCharsBuilder(char[] d1, String d2) throws Exception {
        CharsBuilder appender = new CharsBuilder();
        for (int i = 0; i < DATA_TIMES; i++) {
            appender.append(d1[0]);
            appender.append(d1, 1, d1.length - 1);
            appender.append(d2);
        }
        return appender.toString();
    }

    private String byStringBuilder(char[] d1, String d2) throws Exception {
        StringBuilder appender = new StringBuilder();
        for (int i = 0; i < DATA_TIMES; i++) {
            appender.append(d1[0]);
            appender.append(d1, 1, d1.length - 1);
            appender.append(d2);
        }
        return appender.toString();
    }
}
