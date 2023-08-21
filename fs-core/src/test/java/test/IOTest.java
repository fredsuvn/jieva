package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.io.FsFile;
import xyz.srclab.common.io.FsIO;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;

public class IOTest {

    private static final String DATA = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,!@#$%^&**())$%^&*(*&^<?:LKJHGFDFVGBN" +
        "阿萨法师房间卡死灵法师福卡上积分算法来释放IE覅偶就偶尔见佛耳机佛诶or";

    @Test
    public void testRead() {
        String str = DATA;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes)), bytes);
        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes), 22), Arrays.copyOf(bytes, 22));
        Assert.assertEquals(FsIO.readString(new StringReader(str)), str);
        Assert.assertEquals(FsIO.readString(new StringReader(str), 11), str.substring(0, 11));
        Assert.assertEquals(FsIO.readString(new ByteArrayInputStream(bytes)), str);
        Assert.assertEquals(FsIO.readString(new ByteArrayInputStream(bytes, 0, 8)), str.substring(0, 8));

        byte[] empty = new byte[0];
        InputStream emptyInput = new ByteArrayInputStream(empty);
        Assert.assertNull(FsIO.readBytes(emptyInput));
        Assert.assertNull(FsIO.availableBytes(emptyInput));
        Assert.assertNull(FsIO.readString(emptyInput));
        Assert.assertNull(FsIO.avalaibleString(emptyInput));
        Assert.assertNull(FsIO.readString(FsIO.toReader(emptyInput)));
    }

    @Test
    public void testReadTo() {
        String str = DATA;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FsIO.readBytesTo(new ByteArrayInputStream(bytes), outputStream, -1, 1);
        Assert.assertEquals(outputStream.toByteArray(), bytes);
        outputStream.reset();
        FsIO.readBytesTo(new ByteArrayInputStream(bytes), outputStream, -1, 1024 * 16);
        Assert.assertEquals(outputStream.toByteArray(), bytes);
    }

    @Test
    public void testAvailable() {
        String str = DATA;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        Assert.assertEquals(FsIO.availableBytes(new ByteArrayInputStream(bytes)), bytes);
        Assert.assertEquals(FsIO.avalaibleString(new ByteArrayInputStream(bytes)), str);
        Assert.assertEquals(FsIO.availableBytes(
            new ByteArrayInputStream(bytes, 1, bytes.length)), Arrays.copyOfRange(bytes, 1, bytes.length));
        Assert.assertEquals(FsIO.avalaibleString(
            new ByteArrayInputStream(bytes, 1, bytes.length)), str.substring(1));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FsIO.availableBytesTo(new ByteArrayInputStream(bytes), out);
        Assert.assertEquals(out.toByteArray(), bytes);
        out.reset();
        FsIO.availableBytesTo(new ByteArrayInputStream(bytes, 1, bytes.length), out);
        Assert.assertEquals(out.toByteArray(), Arrays.copyOfRange(bytes, 1, bytes.length));
    }

    @Test
    public void testLimit() throws IOException {
        String str = DATA;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        InputStream in = FsIO.limit(new ByteArrayInputStream(bytes), 6);
        in.skip(5);
        in.read();
        Assert.assertEquals(in.read(), -1);
        Assert.assertEquals(in.read(bytes), -1);
        InputStream in2 = FsIO.limit(new ByteArrayInputStream(bytes), 6);
        Assert.assertEquals(in2.read(bytes), 6);
        Assert.assertEquals(in2.read(), -1);
        Assert.assertEquals(in2.read(bytes), -1);
        OutputStream out = FsIO.limit(new ByteArrayOutputStream(), 6);
        Assert.expectThrows(IOException.class, () -> out.write(bytes));
        out.write(bytes, 0, 6);
        Assert.expectThrows(IOException.class, () -> out.write(1));
    }

    @Test
    public void testWrapper() throws IOException {
        String data = DATA;
        byte[] dataBytes = data.getBytes(FsString.CHARSET);
        File file = new File("IOTest-testWrapper.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        fileOutputStream.write(DATA.getBytes(FsString.CHARSET));
        fileOutputStream.close();
        RandomAccessFile random = new RandomAccessFile(file, "rws");

        testInputStream(data, 3, 100, new ByteArrayInputStream(dataBytes, 3, 100), true);
        testInputStream(data, 0, dataBytes.length, FsIO.toInputStream(ByteBuffer.wrap(data.getBytes(FsString.CHARSET))), true);
        testInputStream(data, 0, dataBytes.length, FsIO.toInputStream(new StringReader(DATA)), false);
        testInputStream(data, 0, dataBytes.length, FsIO.toInputStream(random), true);
        testInputStream(data, 2, 66, FsIO.toInputStream(random, 2, 66), true);
        testInputStream(data, 2, 66, FsIO.limit(new ByteArrayInputStream(dataBytes, 2, 66), 66), false);
        testReader(data, 5, 55, new StringReader(data.substring(5, 5 + 55)), true);
        testReader(data, 0, data.length(), FsIO.toReader(CharBuffer.wrap(DATA)), true);
        testReader(data, 0, data.length(), FsIO.toReader(new ByteArrayInputStream(DATA.getBytes(FsString.CHARSET))), false);

        byte[] bytes = new byte[1024];
        char[] chars = new char[1024];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StringBuilder sb = new StringBuilder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        testOutStream(bytes.length, FsIO.toOutputStream(buffer), (off, len) ->
            Arrays.copyOfRange(bytes, off, off + len));
        testOutStream(bytes.length, FsIO.limit(outputStream, 1024), (off, len) ->
            Arrays.copyOfRange(outputStream.toByteArray(), off, off + len));
        testOutStream(-1, FsIO.toOutputStream(sb), (off, len) ->
            Arrays.copyOfRange(sb.toString().getBytes(FsString.CHARSET), off, off + len));
        testOutStream(-1, FsIO.toOutputStream(random), (off, len) ->
            FsFile.readBytes(file.toPath(), off, len));
        testOutStream(88, FsIO.toOutputStream(random, 8, 88), (off, len) ->
            FsFile.readBytes(file.toPath(), off + 8, len));
        testWriter(FsIO.toWriter(CharBuffer.wrap(chars)), (off, len) ->
            Arrays.copyOfRange(chars, off, off + len));
        outputStream.reset();
        testWriter(FsIO.toWriter(outputStream), (off, len) ->
            new String(outputStream.toByteArray(), FsString.CHARSET).substring(off, off + len).toCharArray());
        random.close();
        file.delete();
    }

    public static void testInputStream(
        String data, int offset, int length,
        InputStream inputStream,
        boolean testMark
    ) throws IOException {
        byte[] bytes = data.getBytes(FsString.CHARSET);
        if (testMark) {
            Assert.assertTrue(inputStream.markSupported());
            long available = inputStream.available();
            Assert.assertEquals(available, length);
            inputStream.mark(length);
            byte[] readBytes = FsIO.readBytes(inputStream, 6);
            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, offset, 6 + offset));
            Assert.assertEquals(inputStream.available(), available - 6);
            inputStream.reset();
            Assert.assertEquals(inputStream.available(), available);
            readBytes = FsIO.readBytes(inputStream, 6);
            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, offset, 6 + offset));
            Assert.assertEquals(inputStream.available(), available - 6);
            readBytes = FsIO.readBytes(inputStream);
            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, 6 + offset, offset + length));
            Assert.assertEquals(inputStream.available(), 0);
            Assert.assertEquals(inputStream.read(), -1);
            Assert.assertEquals(inputStream.available(), 0);
            inputStream.reset();
            inputStream.mark(length);
            inputStream.skip(10);
            readBytes = FsIO.readBytes(inputStream, 6);
            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, 10 + offset, 16 + offset));
            inputStream.reset();
        }
        Assert.assertEquals(inputStream.read(), bytes[offset] & 0x000000ff);
        int k = (int) inputStream.skip(10);
        Assert.assertEquals(FsIO.readBytes(inputStream, 12), Arrays.copyOfRange(bytes, 1 + k + offset, 13 + k + offset));
        Assert.assertEquals(FsIO.readBytes(inputStream), Arrays.copyOfRange(bytes, 13 + k + offset, offset + length));
    }

    public static void testReader(
        String data, int offset, int length,
        Reader reader,
        boolean testMark
    ) throws IOException {
        if (testMark) {
            Assert.assertTrue(reader.markSupported());
            reader.mark(length);
            String readString = FsIO.readString(reader, 6);
            Assert.assertEquals(readString, data.substring(offset, 6 + offset));
            reader.reset();
            readString = FsIO.readString(reader, 6);
            Assert.assertEquals(readString, data.substring(offset, 6 + offset));
            readString = FsIO.readString(reader);
            Assert.assertEquals(readString, data.substring(6 + offset, offset + length));
            Assert.assertEquals(reader.read(), -1);
            reader.reset();
            reader.mark(length);
            reader.skip(10);
            readString = FsIO.readString(reader, 6);
            Assert.assertEquals(readString, data.substring(10 + offset, 16 + offset));
            reader.reset();
        }
        Assert.assertEquals(reader.read(), data.charAt(offset));
        int k = (int) reader.skip(10);
        Assert.assertEquals(FsIO.readString(reader, 12), data.substring(1 + k + offset, 13 + k + offset));
        Assert.assertEquals(FsIO.readString(reader), data.substring(13 + k + offset, offset + length));
    }

    public static void testOutStream(
        long length, OutputStream outputStream, BiFunction<Integer, Integer, byte[]> dest) throws IOException {
        byte[] bytes = length > 0 ? buildRandomBytes((int) length) : buildRandomBytes(1024);
        long remaining = length > 0 ? length : 1024;
        outputStream.write(bytes, 0, 66);
        outputStream.flush();
        Assert.assertEquals(dest.apply(0, 66), Arrays.copyOfRange(bytes, 0, 66));
        remaining -= 66;
        outputStream.write(22);
        outputStream.flush();
        Assert.assertEquals(dest.apply(66, 1), new byte[]{22});
        remaining -= 1;
        outputStream.write(bytes, 0, (int) remaining);
        outputStream.flush();
        Assert.assertEquals(dest.apply(67, (int) remaining), Arrays.copyOfRange(bytes, 0, (int) remaining));
        if (length > 0) {
            Assert.expectThrows(IOException.class, () -> outputStream.write(bytes, 0, bytes.length));
        }
    }

    public static void testWriter(Writer writer, BiFunction<Integer, Integer, char[]> dest) throws IOException {
        char[] chars = DATA.toCharArray();
        writer.write(chars, 0, 66);
        writer.flush();
        Assert.assertEquals(dest.apply(0, 66), Arrays.copyOfRange(chars, 0, 66));
        writer.write(22);
        writer.flush();
        Assert.assertEquals(dest.apply(66, 1), new char[]{22});
        writer.write(chars, 0, chars.length);
        writer.flush();
        Assert.assertEquals(dest.apply(67, chars.length), Arrays.copyOfRange(chars, 0, chars.length));
    }

    @Test
    public void oldTestWrap() throws IOException {
        String base = DATA;
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

    public static byte[] buildRandomBytes(int size) {
        long now = System.currentTimeMillis();
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) ((now * (i + 888) / 999) % 64);
        }
        return bytes;
    }
}
