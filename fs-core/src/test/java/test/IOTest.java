package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.io.FsIO;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class IOTest {

    @Test
    public void testRead() {
        String str = "fdasfsaddfsddd法师法师fsfddddddd打算法法师ddfdfddddddddd发疯dfgsdfeiurqwwwwwwwwwwwwfdfdww法法师wwwwwwwwwfdkjsg";
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes)), bytes);
        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes), 22), Arrays.copyOf(bytes, 22));

        Assert.assertEquals(FsIO.readString(new StringReader(str)), str);
        Assert.assertEquals(FsIO.readString(new StringReader(str), 11), str.substring(0, 11));

        Assert.assertEquals(FsIO.readString(new ByteArrayInputStream(bytes)), str);

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        Assert.assertEquals(FsIO.readString(FsIO.toInputStream(byteBuffer)), str);
        CharBuffer charBuffer = CharBuffer.wrap(str);
        Assert.assertEquals(FsIO.readString(FsIO.toReader(charBuffer)), str);
    }

    @Test
    public void testWrap() throws IOException {
        String base = "e12阿士fds大夫撒fsdf发顺丰是の2饿额ee所发生fsd的法师fasfas法师法师法师";
        StringBuilder text = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4096; i++) {
            text.append(base.charAt(random.nextInt(base.length())));
        }
        String string = text.toString();
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        InputStream in = FsIO.toInputStream(new StringReader(string), StandardCharsets.UTF_8);
        Assert.assertEquals(FsIO.readString(in), string);

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

    @Test
    public void testAvailable() {
        String str = "142894rhiur3n法师法师三d1349r31hye大积分卡上飞机23饿发疯哈三联法计算";
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

        Assert.assertEquals(FsIO.availableBytes(new ByteArrayInputStream(bytes)), bytes);
        Assert.assertEquals(FsIO.avalaibleString(new ByteArrayInputStream(bytes)), str);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FsIO.availableBytesTo(new ByteArrayInputStream(bytes), out);
        Assert.assertEquals(out.toByteArray(), bytes);
    }

    @Test
    public void testRandomAccessFile() throws Exception {
        File file = new File("testRandomAccessFile.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        fileOutputStream.write("0123456789".getBytes());
        fileOutputStream.close();
        RandomAccessFile random = new RandomAccessFile(file, "rws");
        InputStream in = FsIO.toInputStream(random, 1);
        Assert.assertEquals(FsIO.readString(in), "123456789");
        OutputStream out = FsIO.toOutputStream(random, 2);
        out.write("xxx".getBytes());
        in = FsIO.toInputStream(random, 1);
        Assert.assertEquals(FsIO.readString(in), "1xxx56789");
        random.close();
        file.delete();
    }
}
