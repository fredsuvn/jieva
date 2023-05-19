package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.io.FsIO;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class IOTest {

    @Test
    public void testRead() {
        String str = "fdasfsaddddddddddddddddddddddddfgsdfeiurqwwwwwwwwwwwwwwwwwwwwwwwfdkjsg";
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes), true), bytes);
        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes), 22, true), Arrays.copyOf(bytes, 22));

        Assert.assertEquals(FsIO.readString(new StringReader(str), true), str);
        Assert.assertEquals(FsIO.readString(new StringReader(str), 11, true), str.substring(0, 11));

        Assert.assertEquals(FsIO.readString(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8, true), str);

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        Assert.assertEquals(FsIO.readString(FsIO.toInputStream(byteBuffer), StandardCharsets.UTF_8, true), str);
        CharBuffer charBuffer = CharBuffer.wrap(str);
        Assert.assertEquals(FsIO.readString(FsIO.toReader(charBuffer), true), str);
    }

    @Test
    public void testConvert() throws IOException {
        String base = "e12阿士大夫撒发顺丰是の2饿额ee所发生的";
        StringBuilder text = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4096; i++) {
            text.append(base.charAt(random.nextInt(base.length())));
        }
        String string = text.toString();
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        InputStream in = FsIO.toInputStream(new StringReader(string), StandardCharsets.UTF_8);
        Assert.assertEquals(FsIO.readString(in, StandardCharsets.UTF_8, true), string);

        InputStream in2 = FsIO.toInputStream(new StringReader(string), StandardCharsets.UTF_8);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            int b = in2.read();
            if (b != -1) {
                out.write(b);
            } else {
                break;
            }
        }
        Assert.assertEquals(new String(out.toByteArray(), StandardCharsets.UTF_8), string);

        StringWriter sw = new StringWriter();
        OutputStream wo = FsIO.toOutputStream(sw, StandardCharsets.UTF_8);
        wo.write(bytes);
        wo.close();
        Assert.assertEquals(sw.toString(), string);
        StringWriter sw2 = new StringWriter();
        OutputStream wo2 = FsIO.toOutputStream(sw2, StandardCharsets.UTF_8);
        for (byte aByte : bytes) {
            wo2.write(aByte);
        }
        wo2.close();
        Assert.assertEquals(sw2.toString(), string);

        char[] chars = new char[string.length()];
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        FsIO.toWriter(charBuffer).write(string.toCharArray());
        Assert.assertEquals(chars, string.toCharArray());
        byte[] bytes2 = new byte[bytes.length];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes2);
        FsIO.toOutputStream(byteBuffer).write(bytes);
        Assert.assertEquals(bytes2, bytes);
    }
}
