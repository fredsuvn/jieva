package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BRandom;
import xyz.srclab.common.io.BBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author sunqian
 */
public class BufferTest {

    @Test
    public void testReadBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        initBuffer(buffer);
        byte[] bytes = BBuffer.readBytes(buffer);
        Assert.assertEquals(bytes, buffer.array());

        ByteBuffer buffer2 = ByteBuffer.allocateDirect(100);
        initBuffer(buffer2);
        byte[] bytes2 = BBuffer.readBytes(buffer2);
        Assert.assertEquals(bytes2, initArray(new byte[100]));

        byte[] bytes3 = initArray(new byte[100]);
        ByteBuffer buffer3 = ByteBuffer.wrap(bytes3, 10, 90);
        Assert.assertEquals(BBuffer.readBytes(buffer3), Arrays.copyOfRange(bytes3, 10, 100));

        byte[] bytes4 = initArray(new byte[100]);
        ByteBuffer buffer4 = ByteBuffer.wrap(bytes4);
        byte[] bytes4t = BBuffer.readBytes(buffer4, true);
        Assert.assertSame(bytes4t, bytes4);
        Assert.assertEquals(bytes4t, bytes4);
        buffer4.flip();
        byte[] bytes4f = BBuffer.readBytes(buffer4);
        Assert.assertNotSame(bytes4f, bytes4);
        Assert.assertEquals(bytes4f, bytes4);
    }

    @Test
    public void testReadString() {
        byte[] bytes1 = BRandom.randomString(100).getBytes(BDefault.DEFAULT_CHARSET);
        ByteBuffer buffer1 = ByteBuffer.wrap(bytes1, 10, 90);
        Assert.assertEquals(
            BBuffer.readString(buffer1),
            new String(Arrays.copyOfRange(bytes1, 10, 100), BDefault.DEFAULT_CHARSET)
        );
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(99999);
        buffer2.put(bytes1, 10, 90);
        buffer2.flip();
        Assert.assertEquals(
            BBuffer.readString(buffer2),
            new String(Arrays.copyOfRange(bytes1, 10, 100), BDefault.DEFAULT_CHARSET)
        );
    }

    @Test
    public void testGetBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteBuffer getBuffer = BBuffer.readBuffer(buffer, 50);
        Assert.assertEquals(
            getBuffer.array(),
            Arrays.copyOfRange(bytes, 0, 50)
        );
    }

    @Test
    public void testToBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuffer buffer = BBuffer.toBuffer(bytes, true);
        Assert.assertEquals(BBuffer.readBytes(buffer), bytes);
        ByteBuffer buffer2 = BBuffer.toBuffer(bytes);
        Assert.assertEquals(BBuffer.readBytes(buffer2), bytes);
    }

    private void initBuffer(ByteBuffer buffer) {
        for (int i = 0; i < 100; i++) {
            buffer.put((byte) i);
        }
        buffer.flip();
    }

    private byte[] initArray(byte[] array) {
        for (int i = 0; i < 100; i++) {
            array[i] = (byte) i;
        }
        return array;
    }
}
