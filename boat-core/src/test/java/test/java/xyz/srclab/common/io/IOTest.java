package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.io.IOStreams;
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
        InputStream input = IOStreams.toInputStream(text.getBytes());
        String inputString = IOStreams.readString(input);
        input.reset();
        Logs.info("inputString: {}", inputString);
        Assert.assertEquals(inputString, text);
        byte[] bytes = IOStreams.readBytes(input);
        input.reset();
        Assert.assertEquals(bytes, text.getBytes());
        List<String> inputStrings = IOStreams.readLines(input);
        input.reset();
        Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOStreams.readTo(input, output);
        input.reset();
        Assert.assertEquals(output.toByteArray(), bytes);
    }

    @Test
    public void testReader() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = IOStreams.toInputStream(text.getBytes());
        Reader reader = IOStreams.toReader(input);
        String readString = IOStreams.readString(reader);
        input.reset();
        Logs.info("readString: {}", readString);
        Assert.assertEquals(readString, text);
        char[] chars = IOStreams.readString(reader).toCharArray();
        input.reset();
        Assert.assertEquals(chars, text.toCharArray());
        List<String> readStrings = IOStreams.readLines(reader);
        input.reset();
        Assert.assertEquals(readStrings, Arrays.asList("123456", "234567"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Writer writer = IOStreams.toWriter(output);
        IOStreams.readTo(reader, writer);
        input.reset();
        writer.flush();
        Assert.assertEquals(output.toByteArray(), text.getBytes());
    }

    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        initBuffer(buffer);
        byte[] bytes = IOStreams.toBytes(buffer);
        Assert.assertEquals(bytes, buffer.array());
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(100);
        initBuffer(buffer2);
        byte[] bytes2 = IOStreams.toBytes(buffer2);
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
