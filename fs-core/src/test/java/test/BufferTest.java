package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgik.common.base.FsBytesBuilder;
import xyz.fsgik.common.base.FsString;
import xyz.fsgik.common.io.FsBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BufferTest {

    @Test
    public void testRead() {
        ByteBuffer buffer = ByteBuffer.wrap(FsString.encode("123456789"));
        buffer.mark();
        Assert.assertEquals(
            FsString.encode("123456789"),
            FsBuffer.getBytes(buffer)
        );
        Assert.assertEquals(
            buffer.position(),
            buffer.limit()
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("12345"),
            FsBuffer.getBytes(buffer, 5)
        );
        Assert.assertEquals(
            buffer.position(),
            5
        );
        buffer.reset();
        Assert.assertEquals(
            "123456789",
            FsBuffer.getString(buffer)
        );
        Assert.assertEquals(
            buffer.position(),
            buffer.limit()
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("12345"),
            FsBuffer.getBytes(FsBuffer.slice(buffer, 5))
        );
        Assert.assertEquals(
            buffer.position(),
            0
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("56789"),
            FsBuffer.getBytes(FsBuffer.subView(buffer, 4))
        );
        Assert.assertEquals(
            buffer.position(),
            0
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("567"),
            FsBuffer.getBytes(FsBuffer.subView(buffer, 4, 3))
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
            FsBuffer.getBytes(builder.toByteBuffer()),
            FsString.encode("a1234567893456745678956789")
        );
    }

    @Test
    public void testSplit() {
        ByteBuffer buffer = ByteBuffer.wrap(FsString.encode("1234567890"));
        Assert.assertEquals(
            FsBuffer.splitInLength(buffer, 4).stream().map(FsBuffer::getString).collect(Collectors.toList()),
            Arrays.asList("1234", "5678")
        );
        Assert.assertEquals(buffer.position(), 8);
        buffer = ByteBuffer.wrap(FsString.encode("1234567890"));
        Assert.assertEquals(
            FsBuffer.splitInLength(buffer, 5).stream().map(FsBuffer::getString).collect(Collectors.toList()),
            Arrays.asList("12345", "67890")
        );
        Assert.assertEquals(buffer.position(), 10);
        buffer = ByteBuffer.wrap(FsString.encode("123|456|"));
        Assert.assertEquals(
            FsBuffer.splitByDelimiter(buffer, (byte) '|').stream().map(FsBuffer::getString).collect(Collectors.toList()),
            Arrays.asList("123", "456")
        );
        Assert.assertEquals(buffer.position(), 8);
        buffer = ByteBuffer.wrap(FsString.encode("|123|456|"));
        Assert.assertEquals(
            FsBuffer.splitByDelimiter(buffer, (byte) '|').stream().map(FsBuffer::getString).collect(Collectors.toList()),
            Arrays.asList("", "123", "456")
        );
        Assert.assertEquals(buffer.position(), 9);
        buffer = ByteBuffer.wrap(FsString.encode("|123|456"));
        Assert.assertEquals(
            FsBuffer.splitByDelimiter(buffer, (byte) '|').stream().map(FsBuffer::getString).collect(Collectors.toList()),
            Arrays.asList("", "123")
        );
        Assert.assertEquals(buffer.position(), 5);
    }
}