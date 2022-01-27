package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.io.BIO;

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
}
