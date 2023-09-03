package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DataTest {

    @Test
    public void testData() {
        String data = IOTest.DATA;
        byte[] bytes = data.getBytes(FsString.CHARSET);

        //array
        FsData fromBytes = FsData.fromBytes(bytes);
        testArray(fromBytes);
        testWriteArray(fromBytes);
        testBuffer(fromBytes);
        testWriteBuffer(fromBytes);
        testInputStream(fromBytes);
        testOutputStream(fromBytes);

        //buffer
        testArray(FsData.fromBuffer(ByteBuffer.wrap(bytes)));
        testWriteArray(FsData.fromBuffer(ByteBuffer.wrap(bytes)));
        testBuffer(FsData.fromBuffer(ByteBuffer.wrap(bytes)));
        testWriteBuffer(FsData.fromBuffer(ByteBuffer.wrap(bytes)));
        testInputStream(FsData.fromBuffer(ByteBuffer.wrap(bytes)));
        testOutputStream(FsData.fromBuffer(ByteBuffer.wrap(bytes)));

        //input stream
        testArray(FsData.fromStream(new ByteArrayInputStream(bytes)));
        //testWriteArray(FsData.fromStream(new ByteArrayInputStream(bytes)));
        testBuffer(FsData.fromStream(new ByteArrayInputStream(bytes)));
        //testWriteBuffer(FsData.fromStream(new ByteArrayInputStream(bytes)));
        testInputStream(FsData.fromStream(new ByteArrayInputStream(bytes)));
        testOutputStream(FsData.fromStream(new ByteArrayInputStream(bytes)));
    }

    @Test
    public void testMisc() {
        String data = IOTest.DATA;
        byte[] bytes = data.getBytes(FsString.CHARSET);

        //array
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        FsData fromBuffer = FsData.fromBuffer(buffer);
        testWriteBuffer(fromBuffer);
        Assert.assertEquals(buffer.position(), 88);
    }

    private void testArray(FsData data) {
        Assert.assertEquals(data.toString(FsString.CHARSET), IOTest.DATA);
    }

    private void testWriteArray(FsData data) {
        byte[] bytes = IOTest.DATA.getBytes(FsString.CHARSET);
        byte[] dest = new byte[1024];
        int size = data.write(dest, 8, 88);
        Assert.assertEquals(Arrays.copyOfRange(dest, 8, 8 + 88), Arrays.copyOfRange(bytes, 0, 88));
        Assert.assertEquals(size, 88);
    }

    private void testBuffer(FsData data) {
        Assert.assertEquals(data.toBuffer(), ByteBuffer.wrap(IOTest.DATA.getBytes(FsString.CHARSET)));
    }

    private void testWriteBuffer(FsData data) {
        byte[] bytes = IOTest.DATA.getBytes(FsString.CHARSET);
        byte[] dest = new byte[1024];
        int size = data.write(ByteBuffer.wrap(dest, 8, 88));
        Assert.assertEquals(Arrays.copyOfRange(dest, 8, 8 + 88), Arrays.copyOfRange(bytes, 0, 88));
        Assert.assertEquals(size, 88);
    }

    private void testInputStream(FsData data) {
        Assert.assertEquals(FsIO.readString(data.toInputStream()), IOTest.DATA);
    }

    private void testOutputStream(FsData data) {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        long size = data.write(dest);
        Assert.assertEquals(dest.toByteArray(), IOTest.DATA.getBytes(FsString.CHARSET));
        Assert.assertEquals(size, IOTest.DATA.getBytes(FsString.CHARSET).length);
    }
}
