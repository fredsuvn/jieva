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
    public void testByteBuffer() {
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

        byte[] bytes5 = initArray(new byte[100]);
        ByteBuffer buffer5 = ByteBuffer.wrap(bytes5);
        ByteBuffer buffer55 = BBuffer.getBuffer(buffer5, 50);
        Assert.assertEquals(
            buffer55.array(),
            Arrays.copyOfRange(bytes5, 0, 50)
        );
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
