package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.data.GekData;
import xyz.fsgek.common.io.GekIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Supplier;

public class DataTest {

    private static final String DATA = TestUtil.buildRandomString(256, 256);

    public static void testData(byte[] bytes, Supplier<GekData> supplier) {
        testArray(supplier.get(), bytes);
        testWriteArray(supplier.get(), bytes);
        testBuffer(supplier.get(), bytes);
        testWriteBuffer(supplier.get(), bytes);
        testInputStream(supplier.get(), bytes);
        testOutputStream(supplier.get(), bytes);
    }

    private static void testArray(GekData data, byte[] bytes) {
        Assert.assertEquals(data.toString(GekChars.defaultCharset()), DATA);
    }

    private static void testWriteArray(GekData data, byte[] bytes) {
        byte[] dest = new byte[1024];
        int size = data.write(dest, 8, 88);
        Assert.assertEquals(Arrays.copyOfRange(dest, 8, 8 + 88), Arrays.copyOfRange(bytes, 0, 88));
        Assert.assertEquals(size, 88);
    }

    private static void testBuffer(GekData data, byte[] bytes) {
        Assert.assertEquals(data.toBuffer(), ByteBuffer.wrap(bytes));
    }

    private static void testWriteBuffer(GekData data, byte[] bytes) {
        byte[] dest = new byte[1024];
        int size = data.write(ByteBuffer.wrap(dest, 8, 88));
        Assert.assertEquals(Arrays.copyOfRange(dest, 8, 8 + 88), Arrays.copyOfRange(bytes, 0, 88));
        Assert.assertEquals(size, 88);
    }

    private static void testInputStream(GekData data, byte[] bytes) {
        Assert.assertEquals(GekIO.readBytes(data.toInputStream()), bytes);
    }

    private static void testOutputStream(GekData data, byte[] bytes) {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        long size = data.write(dest);
        Assert.assertEquals(dest.toByteArray(), bytes);
        Assert.assertEquals(size, bytes.length);
    }

    @Test
    public void testData() {
        byte[] bytes = DATA.getBytes(GekChars.defaultCharset());
        GekData fromBytes = GekData.wrap(bytes);
        testData(bytes, () -> fromBytes);
        testData(bytes, () -> GekData.wrap(ByteBuffer.wrap(bytes)));
        testData(bytes, () -> GekData.from(new ByteArrayInputStream(bytes)));
        testData(bytes, () -> GekData.from(() -> Arrays.copyOf(bytes, bytes.length)));
    }

    @Test
    public void testMisc() {
        byte[] bytes = DATA.getBytes(GekChars.defaultCharset());
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        GekData fromBuffer = GekData.wrap(buffer);
        testWriteBuffer(fromBuffer, bytes);
        Assert.assertEquals(buffer.position(), 88);
    }
}
