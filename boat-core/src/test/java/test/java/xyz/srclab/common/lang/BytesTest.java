package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Bytes;

import java.nio.ByteBuffer;

public class BytesTest {

    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        initBuffer(buffer);
        byte[] bytes = Bytes.toByteArray(buffer);
        Assert.assertEquals(bytes, buffer.array());
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(100);
        initBuffer(buffer2);
        byte[] bytes2 = Bytes.toByteArray(buffer2);
        Assert.assertEquals(bytes2, initArray(new byte[100]));
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
