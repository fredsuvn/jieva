package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgik.common.base.FsBytes;
import xyz.fsgik.common.base.FsString;

import java.nio.ByteBuffer;

public class BaseTest {

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
            FsBytes.getBytes(FsBytes.subBuffer(buffer, 4))
        );
        Assert.assertEquals(
            buffer.position(),
            0
        );
        buffer.reset();
        Assert.assertEquals(
            FsString.encode("567"),
            FsBytes.getBytes(FsBytes.subBuffer(buffer, 4, 3))
        );
        Assert.assertEquals(
            buffer.position(),
            0
        );
    }
}