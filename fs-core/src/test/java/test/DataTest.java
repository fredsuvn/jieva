package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgik.common.base.FsChars;
import xyz.fsgik.common.data.FsData;
import xyz.fsgik.common.io.FsIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Supplier;

public class DataTest {

    private static final String DATA = TestUtil.buildRandomString(256, 256);

    public static void testData(byte[] bytes, Supplier<FsData> supplier) {
        testArray(supplier.get(), bytes);
        testWriteArray(supplier.get(), bytes);
        testBuffer(supplier.get(), bytes);
        testWriteBuffer(supplier.get(), bytes);
        testInputStream(supplier.get(), bytes);
        testOutputStream(supplier.get(), bytes);
    }

    private static void testArray(FsData data, byte[] bytes) {
        Assert.assertEquals(data.toString(FsChars.defaultCharset()), DATA);
    }

    private static void testWriteArray(FsData data, byte[] bytes) {
        byte[] dest = new byte[1024];
        int size = data.write(dest, 8, 88);
        Assert.assertEquals(Arrays.copyOfRange(dest, 8, 8 + 88), Arrays.copyOfRange(bytes, 0, 88));
        Assert.assertEquals(size, 88);
    }

    private static void testBuffer(FsData data, byte[] bytes) {
        Assert.assertEquals(data.toBuffer(), ByteBuffer.wrap(bytes));
    }

    private static void testWriteBuffer(FsData data, byte[] bytes) {
        byte[] dest = new byte[1024];
        int size = data.write(ByteBuffer.wrap(dest, 8, 88));
        Assert.assertEquals(Arrays.copyOfRange(dest, 8, 8 + 88), Arrays.copyOfRange(bytes, 0, 88));
        Assert.assertEquals(size, 88);
    }

    private static void testInputStream(FsData data, byte[] bytes) {
        Assert.assertEquals(FsIO.readBytes(data.toInputStream()), bytes);
    }

    private static void testOutputStream(FsData data, byte[] bytes) {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        long size = data.write(dest);
        Assert.assertEquals(dest.toByteArray(), bytes);
        Assert.assertEquals(size, bytes.length);
    }

    @Test
    public void testData() {
        byte[] bytes = DATA.getBytes(FsChars.defaultCharset());
        FsData fromBytes = FsData.wrap(bytes);
        testData(bytes, () -> fromBytes);
        testData(bytes, () -> FsData.wrap(ByteBuffer.wrap(bytes)));
        testData(bytes, () -> FsData.from(new ByteArrayInputStream(bytes)));
        testData(bytes, () -> FsData.from(() -> Arrays.copyOf(bytes, bytes.length)));
    }

    @Test
    public void testMisc() {
        byte[] bytes = DATA.getBytes(FsChars.defaultCharset());
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        FsData fromBuffer = FsData.wrap(buffer);
        testWriteBuffer(fromBuffer, bytes);
        Assert.assertEquals(buffer.position(), 88);
    }
}
