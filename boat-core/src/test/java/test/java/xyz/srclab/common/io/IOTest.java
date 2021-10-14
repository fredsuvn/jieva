package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.io.IOs;
import xyz.srclab.common.logging.Logs;

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
        InputStream input = IOs.asInputStream(text.getBytes());
        String inputString = IOs.readString(input);
        input.reset();
        Logs.info("inputString: {}", inputString);
        Assert.assertEquals(inputString, text);
        byte[] bytes = IOs.readBytes(input);
        input.reset();
        Assert.assertEquals(bytes, text.getBytes());
        List<String> inputStrings = IOs.readLines(input);
        input.reset();
        Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOs.readTo(input, output);
        input.reset();
        Assert.assertEquals(output.toByteArray(), bytes);
    }

    @Test
    public void testReader() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = IOs.asInputStream(text.getBytes());
        Reader reader = IOs.toReader(input);
        String readString = IOs.readString(reader);
        input.reset();
        Logs.info("readString: {}", readString);
        Assert.assertEquals(readString, text);
        char[] chars = IOs.readString(reader).toCharArray();
        input.reset();
        Assert.assertEquals(chars, text.toCharArray());
        List<String> readStrings = IOs.readLines(reader);
        input.reset();
        Assert.assertEquals(readStrings, Arrays.asList("123456", "234567"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Writer writer = IOs.toWriter(output);
        IOs.readTo(reader, writer);
        input.reset();
        writer.flush();
        Assert.assertEquals(output.toByteArray(), text.getBytes());
    }

    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        initBuffer(buffer);
        byte[] bytes = IOs.toBytes(buffer);
        Assert.assertEquals(bytes, buffer.array());
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(100);
        initBuffer(buffer2);
        byte[] bytes2 = IOs.toBytes(buffer2);
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
