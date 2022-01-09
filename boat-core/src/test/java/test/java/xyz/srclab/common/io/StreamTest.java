package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.io.BIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class StreamTest {

    @Test
    public void testBytesInputStreamWrapper() throws Exception {
        byte[] array = new byte[]{1, 2, 3, 4};
        InputStream inputStream = BIO.toInputStream(array, 1);
        inputStream.mark(0);
        int b1 = inputStream.read();
        byte[] dest = new byte[3];
        int len1 = inputStream.read(dest, 1, 2);
        Assert.assertEquals(b1, 2);
        Assert.assertEquals(dest, new byte[]{0, 3, 4});
        Assert.assertEquals(len1, 2);
        inputStream.reset();
        Assert.assertEquals(b1, inputStream.read());
        int len2 = inputStream.read(dest, 0, 3);
        Assert.assertEquals(dest, new byte[]{3, 4, 4});
        Assert.assertEquals(len2, 2);
    }

    @Test
    public void testBytesOutputStreamWrapper() throws Exception {
        byte[] array = new byte[4];
        OutputStream outputStream = BIO.toOutputStream(array, 1);
        outputStream.write(1);
        outputStream.write(array, 1, 1);
        outputStream.write(2);
        Assert.assertEquals(array, new byte[]{0, 1, 1, 2});
        Assert.assertThrows(IndexOutOfBoundsException.class, () -> outputStream.write(2));
    }

    @Test
    public void testByteBufferInputStreamWrapper() throws Exception {
        byte[] array = new byte[]{1, 2, 3};
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        InputStream inputStream = BIO.toInputStream(byteBuffer);
        inputStream.mark(0);
        int b1 = inputStream.read();
        byte[] dest = new byte[3];
        int len1 = inputStream.read(dest, 1, 2);
        Assert.assertEquals(b1, 1);
        Assert.assertEquals(dest, new byte[]{0, 2, 3});
        Assert.assertEquals(len1, 2);
        inputStream.reset();
        Assert.assertEquals(b1, inputStream.read());
        int len2 = inputStream.read(dest, 0, 3);
        Assert.assertEquals(dest, new byte[]{2, 3, 3});
        Assert.assertEquals(len2, 2);
    }

    @Test
    public void testByteBufferOutputStream() throws Exception {
        byte[] array = new byte[3];
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        OutputStream outputStream = BIO.toOutputStream(byteBuffer);
        outputStream.write(1);
        outputStream.write(array, 0, 1);
        outputStream.write(2);
        Assert.assertEquals(array, new byte[]{1, 1, 2});
        Assert.assertThrows(BufferOverflowException.class, () -> outputStream.write(2));
    }

    @Test
    public void testCharsReader() throws Exception {
        String str = "123";
        Reader reader = BIO.toReader(str);
        Assert.assertEquals(reader.read(), '1');
        reader.mark(0);
        Assert.assertEquals(reader.read(), '2');
        Assert.assertEquals(reader.read(), '3');
        reader.reset();
        Assert.assertEquals(reader.read(), '2');
        Assert.assertEquals(reader.read(), '3');
    }

    @Test
    public void testAppenderWriter() throws Exception {
        StringBuilder sb = new StringBuilder();
        Writer writer = BIO.toWriter(sb);
        writer.write("hello");
        Assert.assertEquals(sb.toString(), "hello");
    }
}
