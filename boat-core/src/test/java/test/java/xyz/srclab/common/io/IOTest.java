package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.io.IOStreams;
import xyz.srclab.common.test.TestLogger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * @author sunqian
 */
public class IOTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testStream() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = IOStreams.toInputStream(text.getBytes());
        String inputString = IOStreams.readString(input);
        input.reset();
        logger.log("inputString: {}", inputString);
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
        logger.log("readString: {}", readString);
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
}
