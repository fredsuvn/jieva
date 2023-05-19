package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.io.FsIO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class IOTest {

    @Test
    public void read() {
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
    public void readerToStream() {
        String base = "e12阿士大夫撒发顺丰是の2饿额ee所发生的";
        StringBuilder text = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4096; i++) {
            text.append(base.charAt(random.nextInt(base.length())));
        }
        InputStream in = FsIO.toInputStream(new StringReader(text.toString()), StandardCharsets.UTF_8);
        Assert.assertEquals(FsIO.readString(in, StandardCharsets.UTF_8, true), text.toString());
    }
}
