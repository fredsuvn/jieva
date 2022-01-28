package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.io.BBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author sunqian
 */
public class BufferTest {

    @Test
    public void testToBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        initBuffer(buffer);
        byte[] bytes = BBuffer.toBytes(buffer);
        Assert.assertEquals(bytes, buffer.array());

        ByteBuffer buffer2 = ByteBuffer.allocateDirect(100);
        initBuffer(buffer2);
        byte[] bytes2 = BBuffer.toBytes(buffer2);
        Assert.assertEquals(bytes2, initArray(new byte[100]));

        byte[] bytes3 = initArray(new byte[100]);
        ByteBuffer buffer3 = ByteBuffer.wrap(bytes3, 10, 90);
        Assert.assertEquals(BBuffer.toBytes(buffer3), Arrays.copyOfRange(bytes3, 10, 100));

        byte[] bytes4 = initArray(new byte[100]);
        ByteBuffer buffer4 = ByteBuffer.wrap(bytes4);
        byte[] bytes4t = BBuffer.toBytes(buffer4, true);
        Assert.assertSame(bytes4t, bytes4);
        Assert.assertEquals(bytes4t, bytes4);
        buffer4.flip();
        byte[] bytes4f = BBuffer.toBytes(buffer4);
        Assert.assertNotSame(bytes4f, bytes4);
        Assert.assertEquals(bytes4f, bytes4);
    }

    @Test
    public void testGetBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteBuffer getBuffer = BBuffer.getBuffer(buffer, 50);
        Assert.assertEquals(
            getBuffer.array(),
            Arrays.copyOfRange(bytes, 0, 50)
        );
    }

    @Test
    public void testToBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuffer buffer = BBuffer.toBuffer(bytes, true);
        Assert.assertEquals(BBuffer.toBytes(buffer), bytes);
        ByteBuffer buffer2 = BBuffer.toBuffer(bytes);
        Assert.assertEquals(BBuffer.toBytes(buffer2), bytes);
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
