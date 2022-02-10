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
    public void testGetBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        initBuffer(buffer);
        byte[] bytes = BBuffer.getBytes(buffer);
        Assert.assertEquals(bytes, buffer.array());
        buffer.flip();
        Assert.assertEquals(BBuffer.getBytes(buffer, 11), Arrays.copyOfRange(buffer.array(), 0, 11));

        ByteBuffer buffer2 = ByteBuffer.allocateDirect(100);
        initBuffer(buffer2);
        byte[] bytes2 = BBuffer.getBytes(buffer2);
        Assert.assertEquals(bytes2, initArray(new byte[100]));
        buffer2.flip();
        Assert.assertEquals(BBuffer.getBytes(buffer2, 11), Arrays.copyOfRange(bytes2, 0, 11));

        byte[] bytes3 = initArray(new byte[100]);
        ByteBuffer buffer3 = ByteBuffer.wrap(bytes3, 10, 90);
        ByteBuffer buffer33 = BBuffer.getBuffer(bytes3, 10, 90);
        Assert.assertEquals(BBuffer.getBytes(buffer3), Arrays.copyOfRange(bytes3, 10, 100));
        Assert.assertEquals(BBuffer.getBytes(buffer33), Arrays.copyOfRange(bytes3, 10, 100));
        buffer3.flip();
        Assert.assertEquals(BBuffer.getBytes(buffer3, 11), Arrays.copyOfRange(bytes3, 0, 11));
        buffer33.flip();
        Assert.assertEquals(BBuffer.getBytes(buffer33, 11), Arrays.copyOfRange(bytes3, 10, 10 + 11));

        byte[] bytes4 = initArray(new byte[100]);
        ByteBuffer buffer4 = ByteBuffer.wrap(bytes4);
        byte[] bytes4w = BBuffer.getBytes(buffer4, true);
        Assert.assertSame(bytes4w, bytes4);
        Assert.assertEquals(bytes4w, bytes4);
        buffer4.flip();
        byte[] bytes4b = BBuffer.getBytes(buffer4);
        Assert.assertNotSame(bytes4b, bytes4);
        Assert.assertEquals(bytes4b, bytes4);

        buffer.flip();
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BBuffer.getBuffer(buffer, 101, true));
    }

    @Test
    public void testGetString() {
        byte[] bytes1 = BRandom.randomString(100).getBytes(BDefault.DEFAULT_CHARSET);
        ByteBuffer buffer1 = ByteBuffer.wrap(bytes1, 10, 90);
        Assert.assertEquals(
            BBuffer.getString(buffer1, 80),
            new String(Arrays.copyOfRange(bytes1, 10, 90), BDefault.DEFAULT_CHARSET)
        );
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(99999);
        buffer2.put(bytes1, 10, 80);
        buffer2.flip();
        Assert.assertEquals(
            BBuffer.getString(buffer2),
            new String(Arrays.copyOfRange(bytes1, 10, 90), BDefault.DEFAULT_CHARSET)
        );
        buffer1.flip();
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BBuffer.getBuffer(buffer1, 101, true));
    }

    @Test
    public void testGetBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteBuffer get1 = BBuffer.getBuffer(buffer, 50);
        Assert.assertEquals(
            get1.array(),
            Arrays.copyOfRange(bytes, 0, 50)
        );
        ByteBuffer get2 = BBuffer.getBuffer(buffer, 50, true);
        Assert.assertEquals(
            BBuffer.getBytes(get2),
            Arrays.copyOfRange(bytes, 50, 100)
        );
        buffer.flip();
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BBuffer.getBuffer(buffer, 101, true));
    }

    @Test
    public void testBytesGetBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuffer buffer = BBuffer.getBuffer(bytes, true);
        Assert.assertEquals(BBuffer.getBytes(buffer), bytes);
        ByteBuffer buffer2 = BBuffer.getBuffer(bytes);
        Assert.assertEquals(BBuffer.getBytes(buffer2), bytes);
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
