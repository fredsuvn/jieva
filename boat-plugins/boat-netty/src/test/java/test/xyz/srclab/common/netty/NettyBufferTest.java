package test.xyz.srclab.common.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BRandom;
import xyz.srclab.common.netty.BNetty;

import java.util.Arrays;

public class NettyBufferTest {

    @Test
    public void testGetBytes() {
        ByteBuf buffer = Unpooled.buffer(100);
        initBuffer(buffer);
        byte[] bytes = BNetty.getBytes(buffer);
        Assert.assertEquals(bytes, buffer.array());
        buffer.readerIndex(0);
        Assert.assertEquals(BNetty.getBytes(buffer, 11), Arrays.copyOfRange(buffer.array(), 0, 11));

        ByteBuf buffer2 = Unpooled.directBuffer(100);
        initBuffer(buffer2);
        byte[] bytes2 = BNetty.getBytes(buffer2);
        Assert.assertEquals(bytes2, initArray(new byte[100]));
        buffer2.readerIndex(0);
        Assert.assertEquals(BNetty.getBytes(buffer2, 11), Arrays.copyOfRange(bytes2, 0, 11));

        byte[] bytes3 = initArray(new byte[100]);
        ByteBuf buffer3 = Unpooled.wrappedBuffer(bytes3, 10, 90);
        ByteBuf buffer33 = BNetty.getBuffer(bytes3, 10, 90);
        Assert.assertEquals(BNetty.getBytes(buffer3), Arrays.copyOfRange(bytes3, 10, 100));
        Assert.assertEquals(BNetty.getBytes(buffer33), Arrays.copyOfRange(bytes3, 10, 100));
        buffer3.readerIndex(0);
        Assert.assertEquals(BNetty.getBytes(buffer3, 11), Arrays.copyOfRange(bytes3, 10, 10 + 11));
        buffer33.readerIndex(0);
        Assert.assertEquals(BNetty.getBytes(buffer33, 11), Arrays.copyOfRange(bytes3, 10, 10 + 11));

        byte[] bytes4 = initArray(new byte[100]);
        ByteBuf buffer4 = Unpooled.wrappedBuffer(bytes4);
        byte[] bytes4w = BNetty.getBytes(buffer4, true);
        Assert.assertSame(bytes4w, bytes4);
        Assert.assertEquals(bytes4w, bytes4);
        buffer4.readerIndex(0);
        byte[] bytes4b = BNetty.getBytes(buffer4);
        Assert.assertNotSame(bytes4b, bytes4);
        Assert.assertEquals(bytes4b, bytes4);

        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BNetty.getBuffer(buffer, 101, true));
    }

    @Test
    public void testGetString() {
        byte[] bytes1 = BRandom.randomString(100).getBytes(BDefault.DEFAULT_CHARSET);
        ByteBuf buffer1 = Unpooled.wrappedBuffer(bytes1, 10, 90);
        Assert.assertEquals(
            BNetty.getString(buffer1, 80),
            new String(Arrays.copyOfRange(bytes1, 10, 90), BDefault.DEFAULT_CHARSET)
        );
        ByteBuf buffer2 = Unpooled.directBuffer(99999);
        buffer2.writeBytes(bytes1, 10, 80);
        buffer2.readerIndex(0);
        Assert.assertEquals(
            BNetty.getString(buffer2),
            new String(Arrays.copyOfRange(bytes1, 10, 90), BDefault.DEFAULT_CHARSET)
        );
        buffer1.readerIndex(0);
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BNetty.getBuffer(buffer1, 101, true));
    }

    @Test
    public void testGetBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
        ByteBuf get1 = BNetty.getBuffer(buffer, 50);
        Assert.assertEquals(
            get1.array(),
            Arrays.copyOfRange(bytes, 0, 50)
        );
        ByteBuf get2 = BNetty.getBuffer(buffer, 50, true);
        Assert.assertEquals(
            BNetty.getBytes(get2),
            Arrays.copyOfRange(bytes, 50, 100)
        );
        buffer.readerIndex(0);
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BNetty.getBuffer(buffer, 101, true));
    }

    @Test
    public void testBytesGetBuffer() {
        byte[] bytes = initArray(new byte[100]);
        ByteBuf buffer = BNetty.getBuffer(bytes, true);
        Assert.assertEquals(BNetty.getBytes(buffer), bytes);
        ByteBuf buffer2 = BNetty.getBuffer(bytes);
        Assert.assertEquals(BNetty.getBytes(buffer2), bytes);
    }

    private void initBuffer(ByteBuf buffer) {
        for (int i = 0; i < 100; i++) {
            buffer.writeByte((byte) i);
        }
    }

    private byte[] initArray(byte[] array) {
        for (int i = 0; i < 100; i++) {
            array[i] = (byte) i;
        }
        return array;
    }
}
