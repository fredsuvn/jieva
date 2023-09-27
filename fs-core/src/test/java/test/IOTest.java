package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fs404.common.base.FsString;
import xyz.fs404.common.io.FsIO;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;

public class IOTest {

    private static final String DATA = TestUtil.buildRandomString(256, 256);

    public static void testInputStream(
        String data,
        int offset,
        int length,
        InputStream inputStream,
        boolean testMark
    ) throws IOException {
        byte[] bytes = data.getBytes(FsString.CHARSET);
        if (length < 128 || bytes.length - offset < length) {
            throw new IllegalArgumentException("Data length not enough!");
        }
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
        String data,
        int offset,
        int length,
        Reader reader,
        boolean testMark
    ) throws IOException {
        if (length < 128 || data.length() - offset < length) {
            throw new IllegalArgumentException("Data length not enough!");
        }
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
        long limit,
        OutputStream outputStream,
        BiFunction<Integer, Integer, byte[]> dest
    ) throws IOException {
        if (limit != -1 && limit < 128) {
            throw new IllegalArgumentException("Written length must >= 128 or -1!");
        }
        byte[] bytes = limit > 0 ? TestUtil.buildRandomBytes((int) limit) : TestUtil.buildRandomBytes(1024);
        long remaining = bytes.length;
        outputStream.write(bytes, 0, 66);
        outputStream.flush();
        Assert.assertEquals(dest.apply(0, 66), Arrays.copyOfRange(bytes, 0, 66));
        remaining -= 66;
        outputStream.write(22);
        outputStream.flush();
        Assert.assertEquals(dest.apply(66, 1), new byte[]{22});
        remaining -= 1;
        int writeSize = (int) (remaining - 8);
        outputStream.write(bytes, 8, writeSize);
        outputStream.flush();
        Assert.assertEquals(dest.apply(67, writeSize), Arrays.copyOfRange(bytes, 8, 8 + writeSize));
        remaining -= writeSize;
        outputStream.write(bytes, 0, (int) remaining);
        outputStream.flush();
        Assert.assertEquals(dest.apply(67 + writeSize, (int) remaining), Arrays.copyOfRange(bytes, 0, (int) remaining));
        if (limit > 0) {
            Assert.expectThrows(IOException.class, () -> outputStream.write(bytes, 0, bytes.length));
        }
    }

    public static void testWriter(
        Writer writer,
        BiFunction<Integer, Integer, char[]> dest
    ) throws IOException {
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
    public void testRead() {
        String str = DATA;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes)), bytes);
        Assert.assertEquals(FsIO.readBytes(new ByteArrayInputStream(bytes), 22), Arrays.copyOf(bytes, 22));
        Assert.assertEquals(FsIO.readString(new StringReader(str)), str);
        Assert.assertEquals(FsIO.readString(new StringReader(str), 11), str.substring(0, 11));
        Assert.assertEquals(FsIO.readString(new ByteArrayInputStream(bytes)), str);
        Assert.assertEquals(FsIO.readString(new ByteArrayInputStream(bytes, 0, 8)), str.substring(0, 8));
        Assert.assertEquals(FsIO.readBytes(new TestInput(new ByteArrayInputStream(bytes))), bytes);
        Assert.assertEquals(FsIO.readBytes(new TestInput(new ByteArrayInputStream(bytes)), 22), Arrays.copyOf(bytes, 22));

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
        Assert.assertEquals(FsIO.availableBytes(new TestInput(new ByteArrayInputStream(bytes))), Arrays.copyOf(bytes, 1));
    }

    @Test
    public void testLimit() throws IOException {
        String str = DATA;
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        InputStream in = FsIO.limited(new ByteArrayInputStream(bytes), 6);
        in.skip(5);
        in.read();
        Assert.assertEquals(in.read(), -1);
        Assert.assertEquals(in.read(bytes), -1);
        InputStream in2 = FsIO.limited(new ByteArrayInputStream(bytes), 6);
        Assert.assertEquals(in2.read(bytes), 6);
        Assert.assertEquals(in2.read(), -1);
        Assert.assertEquals(in2.read(bytes), -1);
        OutputStream out = FsIO.limited(new ByteArrayOutputStream(), 6);
        Assert.expectThrows(IOException.class, () -> out.write(bytes));
        out.write(bytes, 0, 6);
        Assert.expectThrows(IOException.class, () -> out.write(1));
    }

    @Test
    public void testWrapper() throws IOException {
        String data = DATA;
        byte[] dataBytes = data.getBytes(FsString.CHARSET);
        File file = FileTest.createFile("IOTest-testWrapper.txt", data);
        RandomAccessFile random = new RandomAccessFile(file, "rws");

        testInputStream(data, 3, 222, FsIO.toInputStream(dataBytes, 3, 222), true);
        testInputStream(data, 0, dataBytes.length, FsIO.toInputStream(ByteBuffer.wrap(data.getBytes(FsString.CHARSET))), true);
        testInputStream(data, 0, dataBytes.length, FsIO.toInputStream(new StringReader(DATA)), false);
        testInputStream(data, 0, dataBytes.length, FsIO.toInputStream(random), true);
        testInputStream(data, 2, 131, FsIO.toInputStream(random, 2, 131), true);
        testInputStream(data, 2, 131, FsIO.limited(FsIO.toInputStream(dataBytes, 2, 131), 131), false);
        testReader(data, 5, 155, new StringReader(data.substring(5, 5 + 155)), true);
        testReader(data, 0, data.length(), FsIO.toReader(CharBuffer.wrap(DATA)), true);
        testReader(data, 0, data.length(), FsIO.toReader(new ByteArrayInputStream(DATA.getBytes(FsString.CHARSET))), false);

        byte[] bytes = new byte[1024];
        char[] chars = new char[1024];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        StringBuilder sb = new StringBuilder();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        testOutStream(bytes.length, FsIO.toOutputStream(buffer), (off, len) ->
            Arrays.copyOfRange(bytes, off, off + len));
        testOutStream(bytes.length, FsIO.limited(outputStream, 1024), (off, len) ->
            Arrays.copyOfRange(outputStream.toByteArray(), off, off + len));
        testOutStream(-1, FsIO.toOutputStream(sb), (off, len) ->
            Arrays.copyOfRange(sb.toString().getBytes(FsString.CHARSET), off, off + len));
        testOutStream(-1, FsIO.toOutputStream(random), (off, len) ->
            FsIO.readBytes(file.toPath(), off, len));
        testOutStream(188, FsIO.toOutputStream(random, 8, 188), (off, len) ->
            FsIO.readBytes(file.toPath(), off + 8, len));
        testOutStream(-1, FsIO.toOutputStream(random, 8, -1), (off, len) ->
            FsIO.readBytes(file.toPath(), off + 8, len));
        byte[] back = new byte[2048];
        testOutStream(bytes.length, FsIO.toOutputStream(back, 111, 1024), (off, len) ->
            Arrays.copyOfRange(back, off + 111, off + 111 + len));
        testWriter(FsIO.toWriter(CharBuffer.wrap(chars)), (off, len) ->
            Arrays.copyOfRange(chars, off, off + len));
        outputStream.reset();
        testWriter(FsIO.toWriter(outputStream), (off, len) ->
            new String(outputStream.toByteArray(), FsString.CHARSET).substring(off, off + len).toCharArray());
        random.close();
        file.delete();
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

    @Test
    public void testReadFile() throws IOException {
        String data = DATA;
        File file = FileTest.createFile("IOTest-testReadFile.txt", data);
        Assert.assertEquals(FsIO.readString(file.toPath()), data);
        Assert.assertEquals(
            FsIO.readString(file.toPath(), 18, 36),
            new String(data.getBytes(FsString.CHARSET), 18, 36)
        );
        file.delete();
    }

    @Test
    public void testWriteFile() throws IOException {
        File file = FileTest.createFile("IOTest-testWriteFile.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FsIO.writeString(file.toPath(), "lalala");
        FsIO.writeString(file.toPath(), 6, 18, "222");
        Assert.assertEquals(FsIO.readString(file.toPath()), "lalala222");
        FsIO.writeString(file.toPath(), 6, 7, "1");
        Assert.assertEquals(FsIO.readString(file.toPath()), "lalala122");
        FsIO.writeString(file.toPath(), 7, 100, "3333中文中文");
        Assert.assertEquals(FsIO.readString(file.toPath()), "lalala13333中文中文");
        file.delete();
    }

    private static final class TestInput extends InputStream {

        private final InputStream in;

        private TestInput(InputStream in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return in.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return in.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return in.skip(n);
        }

        @Override
        public int available() throws IOException {
            int a = in.available();
            if (a <= 0) {
                return a;
            }
            return 1;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }

        @Override
        public void mark(int readlimit) {
            in.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            in.reset();
        }

        @Override
        public boolean markSupported() {
            return in.markSupported();
        }
    }
}
