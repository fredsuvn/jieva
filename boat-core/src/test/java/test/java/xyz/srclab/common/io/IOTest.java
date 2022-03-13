package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BString;
import xyz.srclab.common.io.*;

import java.io.*;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * @author sunqian
 */
public class IOTest {

    @Test
    public void testStream() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = BIO.asInputStream(text.getBytes());
        String inputString = BIO.readString(input);
        input.reset();
        BLog.info("inputString: {}", inputString);
        Assert.assertEquals(inputString, text);
        byte[] bytes = BIO.readBytes(input);
        input.reset();
        Assert.assertEquals(bytes, text.getBytes());
        List<String> inputStrings = BIO.readLines(input);
        input.reset();
        Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567", ""));//note end with \r\n
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BIO.readTo(input, output);
        input.reset();
        Assert.assertEquals(output.toByteArray(), bytes);
    }

    @Test
    public void testReader() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = BIO.asInputStream(text.getBytes());
        Reader reader = BIO.asReader(input);
        String readString = BIO.readString(reader);
        input.reset();
        BLog.info("readString: {}", readString);
        Assert.assertEquals(readString, text);
        char[] chars = BIO.readString(reader).toCharArray();
        input.reset();
        Assert.assertEquals(chars, text.toCharArray());
        List<String> readStrings = BIO.readLines(reader);
        input.reset();
        Assert.assertEquals(readStrings, Arrays.asList("123456", "234567", ""));//note end with \r\n
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Writer writer = BIO.asWriter(output);
        BIO.readTo(reader, writer);
        input.reset();
        writer.flush();
        Assert.assertEquals(output.toByteArray(), text.getBytes());
    }

    @Test
    public void testBytesInputStreamWrapper() throws Exception {
        byte[] array = new byte[]{1, 2, 3, 4};
        InputStream inputStream = BIO.asInputStream(array, 1);
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
        OutputStream outputStream = BIO.asOutputStream(array, 1);
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
        InputStream inputStream = BBuffer.asInputStream(byteBuffer);
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
        OutputStream outputStream = BBuffer.asOutputStream(byteBuffer);
        outputStream.write(1);
        outputStream.write(array, 0, 1);
        outputStream.write(2);
        Assert.assertEquals(array, new byte[]{1, 1, 2});
        Assert.assertThrows(BufferOverflowException.class, () -> outputStream.write(2));
    }

    @Test
    public void testCharsReader() throws Exception {
        String str = "123";
        Reader reader = BIO.asReader(str);
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
        Writer writer = BIO.asWriter(sb);
        writer.write("hello");
        Assert.assertEquals(sb.toString(), "hello");
    }

    @Test
    public void testBytesAppender() {
        BytesAppender bytesAppender = new BytesAppender();
        bytesAppender.append('a');
        bytesAppender.append(ByteBuffer.wrap(new byte[]{'b', 'c'}));
        bytesAppender.append(new byte[]{'d', 'e'}, 1);
        Assert.assertEquals(BString.toString8Bit(bytesAppender.toBytes()), "abce");
    }

    @Test
    public void testUnclose() throws Exception {
        TestUncloseInStream ti = new TestUncloseInStream();
        InputStream ui = BIO.unclose(ti);
        ui.close();
        Assert.assertFalse(ti.isClose());

        TestUncloseOutStream to = new TestUncloseOutStream();
        OutputStream uo = BIO.unclose(to);
        uo.close();
        Assert.assertFalse(to.isClose());

        ByteArrayInputStream bip = new ByteArrayInputStream(new byte[100]);
        UncloseInputStream<ByteArrayInputStream> ubip = BIO.unclose(bip);
        ubip.read(new byte[50]);
        Assert.assertEquals(ubip.getCount(), 50);
        ubip.mark(50);
        ubip.read(new byte[50]);
        Assert.assertEquals(ubip.getCount(), 100);
        ubip.reset();
        Assert.assertEquals(ubip.getCount(), 50);

        ByteArrayOutputStream bop = new ByteArrayOutputStream();
        UncloseOutputStream<ByteArrayOutputStream> ubop = BIO.unclose(bop);
        ubop.write(1);
        ubop.write(new byte[50]);
        ubop.write(new byte[50], 10, 20);
        Assert.assertEquals(ubop.getCount(), 71);
    }

    private static class TestUncloseInStream extends InputStream {

        private boolean isClose = false;

        @Override
        public int read() throws IOException {
            return 0;
        }

        @Override
        public void close() throws IOException {
            isClose = true;
        }

        public boolean isClose() {
            return isClose;
        }
    }

    private static class TestUncloseOutStream extends OutputStream {

        private boolean isClose = false;

        @Override
        public void write(int b) throws IOException {
        }

        @Override
        public void close() throws IOException {
            isClose = true;
        }

        public boolean isClose() {
            return isClose;
        }
    }
}
