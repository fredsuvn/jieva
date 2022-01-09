package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.io.BIO;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
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
        InputStream input = BIO.toInputStream(text.getBytes());
        String inputString = BIO.readString(input);
        input.reset();
        BLog.info("inputString: {}", inputString);
        Assert.assertEquals(inputString, text);
        byte[] bytes = BIO.readBytes(input);
        input.reset();
        Assert.assertEquals(bytes, text.getBytes());
        List<String> inputStrings = BIO.readLines(input);
        input.reset();
        Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BIO.readTo(input, output);
        input.reset();
        Assert.assertEquals(output.toByteArray(), bytes);
    }

    @Test
    public void testReader() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = BIO.toInputStream(text.getBytes());
        Reader reader = BIO.toReader(input);
        String readString = BIO.readString(reader);
        input.reset();
        BLog.info("readString: {}", readString);
        Assert.assertEquals(readString, text);
        char[] chars = BIO.readString(reader).toCharArray();
        input.reset();
        Assert.assertEquals(chars, text.toCharArray());
        List<String> readStrings = BIO.readLines(reader);
        input.reset();
        Assert.assertEquals(readStrings, Arrays.asList("123456", "234567"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Writer writer = BIO.toWriter(output);
        BIO.readTo(reader, writer);
        input.reset();
        writer.flush();
        Assert.assertEquals(output.toByteArray(), text.getBytes());
    }

    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        initBuffer(buffer);
        byte[] bytes = BIO.toBytes(buffer);
        Assert.assertEquals(bytes, buffer.array());
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(100);
        initBuffer(buffer2);
        byte[] bytes2 = BIO.toBytes(buffer2);
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
