package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgik.common.base.FsBytes;
import xyz.fsgik.common.base.FsBytesBuilder;
import xyz.fsgik.common.base.FsString;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BytesTest {

    @Test
    public void testBytes() {
        ByteBuffer buffer = ByteBuffer.wrap(FsString.encode("123456789"));
        buffer.mark();
        Assert.assertEquals(
            FsString.encode("123456789"),
            FsBytes.getBytes(buffer)
        );
        Assert.assertEquals(
            buffer.position(),
            buffer.limit()
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("12345"),
            FsBytes.getBytes(buffer, 5)
        );
        Assert.assertEquals(
            buffer.position(),
            5
        );
        buffer.reset();
        Assert.assertEquals(
            "123456789",
            FsBytes.getString(buffer)
        );
        Assert.assertEquals(
            buffer.position(),
            buffer.limit()
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("12345"),
            FsBytes.getBytes(FsBytes.slice(buffer, 5))
        );
        Assert.assertEquals(
            buffer.position(),
            0
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("56789"),
            FsBytes.getBytes(FsBytes.subView(buffer, 4))
        );
        Assert.assertEquals(
            buffer.position(),
            0
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("567"),
            FsBytes.getBytes(FsBytes.subView(buffer, 4, 3))
        );
        Assert.assertEquals(
            buffer.position(),
            0
        );
    }

    @Test
    public void testBytesBuilder() {
        FsBytesBuilder builder = new FsBytesBuilder();
        builder.append((byte) 'a');
        builder.append(FsString.encode("123456789"));
        builder.append(FsString.encode("123456789"), 2, 5);
        ByteBuffer buffer1 = ByteBuffer.wrap(FsString.encode("123456789"));
        Assert.assertTrue(buffer1.hasArray());
        buffer1.mark();
        buffer1.position(3);
        builder.append(buffer1);
        buffer1.reset();
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(9);
        Assert.assertFalse(buffer2.hasArray());
        buffer2.put(buffer1);
        buffer2.flip();
        buffer2.position(4);
        builder.append(buffer2);
        Assert.assertEquals(
            FsBytes.getBytes(builder.toByteBuffer()),
            FsString.encode("a1234567893456745678956789")
        );
    }

    @Test
    public void testBytesSplit() {
        ByteBuffer buffer = ByteBuffer.wrap(FsString.encode("1234567890"));
        Assert.assertEquals(
            FsBytes.splitInLength(buffer, 4).stream().map(FsBytes::getString).collect(Collectors.toList()),
            Arrays.asList("1234", "5678")
        );
        Assert.assertEquals(buffer.position(), 8);
        buffer = ByteBuffer.wrap(FsString.encode("1234567890"));
        Assert.assertEquals(
            FsBytes.splitInLength(buffer, 5).stream().map(FsBytes::getString).collect(Collectors.toList()),
            Arrays.asList("12345", "67890")
        );
        Assert.assertEquals(buffer.position(), 10);
    }
}