//package test;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.fslabo.common.base.JieChars;
//import xyz.fslabo.common.file.JieFile;
//import xyz.fslabo.common.io.JieIO;
//
//import java.io.*;
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.Random;
//import java.util.function.BiFunction;
//
//public class IOTest {
//
//    private static final String DATA = TestUtil.buildRandomString(256, 256);
//
//    public static void testInputStream(
//        String data,
//        int offset,
//        int length,
//        InputStream inputStream,
//        boolean testMark
//    ) throws IOException {
//        byte[] bytes = data.getBytes(JieChars.defaultCharset());
//        if (length < 128 || bytes.length - offset < length) {
//            throw new IllegalArgumentException("Data length not enough!");
//        }
//        if (testMark) {
//            Assert.assertTrue(inputStream.markSupported());
//            long available = inputStream.available();
//            Assert.assertEquals(available, length);
//            inputStream.mark(length);
//            byte[] readBytes = JieIO.read(inputStream, 6);
//            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, offset, 6 + offset));
//            Assert.assertEquals(inputStream.available(), available - 6);
//            inputStream.reset();
//            Assert.assertEquals(inputStream.available(), available);
//            readBytes = JieIO.read(inputStream, 6);
//            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, offset, 6 + offset));
//            Assert.assertEquals(inputStream.available(), available - 6);
//            readBytes = JieIO.read(inputStream);
//            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, 6 + offset, offset + length));
//            Assert.assertEquals(inputStream.available(), 0);
//            Assert.assertEquals(inputStream.read(), -1);
//            Assert.assertEquals(inputStream.available(), 0);
//            inputStream.reset();
//            inputStream.mark(length);
//            inputStream.skip(10);
//            readBytes = JieIO.read(inputStream, 6);
//            Assert.assertEquals(readBytes, Arrays.copyOfRange(bytes, 10 + offset, 16 + offset));
//            inputStream.reset();
//        }
//        Assert.assertEquals(inputStream.read(), bytes[offset] & 0x000000ff);
//        int k = (int) inputStream.skip(10);
//        Assert.assertEquals(JieIO.read(inputStream, 12), Arrays.copyOfRange(bytes, 1 + k + offset, 13 + k + offset));
//        Assert.assertEquals(JieIO.read(inputStream), Arrays.copyOfRange(bytes, 13 + k + offset, offset + length));
//    }
//
//    public static void testReader(
//        String data,
//        int offset,
//        int length,
//        Reader reader,
//        boolean testMark
//    ) throws IOException {
//        if (length < 128 || data.length() - offset < length) {
//            throw new IllegalArgumentException("Data length not enough!");
//        }
//        if (testMark) {
//            Assert.assertTrue(reader.markSupported());
//            reader.mark(length);
//            String readString = JieIO.read(reader, 6);
//            Assert.assertEquals(readString, data.substring(offset, 6 + offset));
//            reader.reset();
//            readString = JieIO.read(reader, 6);
//            Assert.assertEquals(readString, data.substring(offset, 6 + offset));
//            readString = JieIO.read(reader);
//            Assert.assertEquals(readString, data.substring(6 + offset, offset + length));
//            Assert.assertEquals(reader.read(), -1);
//            reader.reset();
//            reader.mark(length);
//            reader.skip(10);
//            readString = JieIO.read(reader, 6);
//            Assert.assertEquals(readString, data.substring(10 + offset, 16 + offset));
//            reader.reset();
//        }
//        Assert.assertEquals(reader.read(), data.charAt(offset));
//        int k = (int) reader.skip(10);
//        Assert.assertEquals(JieIO.read(reader, 12), data.substring(1 + k + offset, 13 + k + offset));
//        Assert.assertEquals(JieIO.read(reader), data.substring(13 + k + offset, offset + length));
//    }
//
//    public static void testOutStream(
//        long limit,
//        OutputStream outputStream,
//        BiFunction<Integer, Integer, byte[]> dest
//    ) throws IOException {
//        if (limit != -1 && limit < 128) {
//            throw new IllegalArgumentException("Written length must >= 128 or -1!");
//        }
//        byte[] bytes = limit > 0 ? TestUtil.buildRandomBytes((int) limit) : TestUtil.buildRandomBytes(1024);
//        long remaining = bytes.length;
//        outputStream.write(bytes, 0, 66);
//        outputStream.flush();
//        Assert.assertEquals(dest.apply(0, 66), Arrays.copyOfRange(bytes, 0, 66));
//        remaining -= 66;
//        outputStream.write(22);
//        outputStream.flush();
//        Assert.assertEquals(dest.apply(66, 1), new byte[]{22});
//        remaining -= 1;
//        int writeSize = (int) (remaining - 8);
//        outputStream.write(bytes, 8, writeSize);
//        outputStream.flush();
//        Assert.assertEquals(dest.apply(67, writeSize), Arrays.copyOfRange(bytes, 8, 8 + writeSize));
//        remaining -= writeSize;
//        outputStream.write(bytes, 0, (int) remaining);
//        outputStream.flush();
//        Assert.assertEquals(dest.apply(67 + writeSize, (int) remaining), Arrays.copyOfRange(bytes, 0, (int) remaining));
//        if (limit > 0) {
//            Assert.expectThrows(IOException.class, () -> outputStream.write(bytes, 0, bytes.length));
//        }
//    }
//
//    public static void testWriter(
//        Writer writer,
//        BiFunction<Integer, Integer, char[]> dest
//    ) throws IOException {
//        char[] chars = DATA.toCharArray();
//        writer.write(chars, 0, 66);
//        writer.flush();
//        Assert.assertEquals(dest.apply(0, 66), Arrays.copyOfRange(chars, 0, 66));
//        writer.write(22);
//        writer.flush();
//        Assert.assertEquals(dest.apply(66, 1), new char[]{22});
//        writer.write(chars, 0, chars.length);
//        writer.flush();
//        Assert.assertEquals(dest.apply(67, chars.length), Arrays.copyOfRange(chars, 0, chars.length));
//    }
//
//    @Test
//    public void testRead() {
//        String str = DATA;
//        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
//        Assert.assertEquals(JieIO.read(new ByteArrayInputStream(bytes)), bytes);
//        Assert.assertEquals(JieIO.read(new ByteArrayInputStream(bytes), 22), Arrays.copyOf(bytes, 22));
//        Assert.assertEquals(JieIO.read(new StringReader(str)), str);
//        Assert.assertEquals(JieIO.read(new StringReader(str), 11), str.substring(0, 11));
//        Assert.assertEquals(JieIO.readString(new ByteArrayInputStream(bytes)), str);
//        Assert.assertEquals(JieIO.readString(new ByteArrayInputStream(bytes, 0, 8)), str.substring(0, 8));
//        Assert.assertEquals(JieIO.read(new TestInput(new ByteArrayInputStream(bytes))), bytes);
//        Assert.assertEquals(JieIO.read(new TestInput(new ByteArrayInputStream(bytes)), 22), Arrays.copyOf(bytes, 22));
//
//        byte[] empty = new byte[0];
//        InputStream emptyInput = new ByteArrayInputStream(empty);
//        Assert.assertNull(JieIO.read(emptyInput));
//        Assert.assertNull(JieIO.available(emptyInput));
//        Assert.assertNull(JieIO.readString(emptyInput));
//        Assert.assertNull(JieIO.avalaibleString(emptyInput));
//        Assert.assertNull(JieIO.read(JieIO.toReader(emptyInput)));
//    }
//
//    @Test
//    public void testReadTo() {
//        String str = DATA;
//        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        JieIO.readTo(new ByteArrayInputStream(bytes), outputStream, -1, 1);
//        Assert.assertEquals(outputStream.toByteArray(), bytes);
//        outputStream.reset();
//        JieIO.readTo(new ByteArrayInputStream(bytes), outputStream, -1, 1024 * 16);
//        Assert.assertEquals(outputStream.toByteArray(), bytes);
//        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
//        JieIO.readTo(new ByteArrayInputStream(bytes), buffer);
//        buffer.flip();
//        Assert.assertEquals(JieIO.read(buffer), bytes);
//        buffer = ByteBuffer.allocate(bytes.length);
//        JieIO.readTo(new ByteArrayInputStream(bytes), buffer);
//        buffer.flip();
//        Assert.assertEquals(JieIO.read(buffer), bytes);
//        ByteBuffer source = ByteBuffer.wrap(bytes);
//        buffer = ByteBuffer.allocate(bytes.length);
//        JieIO.readTo(source, buffer);
//        buffer.flip();
//        Assert.assertEquals(JieIO.read(buffer), bytes);
//        Assert.assertEquals(source.remaining(), 0);
//        source.flip();
//        buffer = ByteBuffer.allocate(bytes.length);
//        JieIO.readTo(source, buffer, 2);
//        buffer.flip();
//        Assert.assertEquals(JieIO.read(buffer), Arrays.copyOf(bytes, 2));
//        Assert.assertEquals(source.remaining(), bytes.length - 2);
//
//        byte[] dest = new byte[bytes.length * 2];
//        buffer = ByteBuffer.wrap(bytes);
//        int size = JieIO.readTo(buffer, dest);
//        Assert.assertEquals(buffer.position(), bytes.length);
//        Assert.assertEquals(size, bytes.length);
//        Assert.assertEquals(bytes, Arrays.copyOf(dest, bytes.length));
//        buffer = ByteBuffer.wrap(bytes, 10, 10);
//        size = JieIO.readTo(ByteBuffer.wrap(bytes, 10, 10), dest, 5);
//        Assert.assertEquals(buffer.position(), 10);
//        Assert.assertEquals(size, 10);
//        Assert.assertEquals(Arrays.copyOfRange(bytes, 10, 10 + 10), Arrays.copyOfRange(dest, 5, 5 + 10));
//    }
//
//    @Test
//    public void testAvailable() {
//        String str = DATA;
//        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
//        Assert.assertEquals(JieIO.available(new ByteArrayInputStream(bytes)), bytes);
//        Assert.assertEquals(JieIO.avalaibleString(new ByteArrayInputStream(bytes)), str);
//        Assert.assertEquals(JieIO.available(
//            new ByteArrayInputStream(bytes, 1, bytes.length)), Arrays.copyOfRange(bytes, 1, bytes.length));
//        Assert.assertEquals(JieIO.avalaibleString(
//            new ByteArrayInputStream(bytes, 1, bytes.length)), str.substring(1));
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        JieIO.availableTo(new ByteArrayInputStream(bytes), out);
//        Assert.assertEquals(out.toByteArray(), bytes);
//        out.reset();
//        JieIO.availableTo(new ByteArrayInputStream(bytes, 1, bytes.length), out);
//        Assert.assertEquals(out.toByteArray(), Arrays.copyOfRange(bytes, 1, bytes.length));
//        Assert.assertEquals(JieIO.available(new TestInput(new ByteArrayInputStream(bytes))), Arrays.copyOf(bytes, 1));
//    }
//
//    @Test
//    public void testLimit() throws IOException {
//        String str = DATA;
//        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
//        InputStream in = JieIO.limit(new ByteArrayInputStream(bytes), 6);
//        in.skip(5);
//        in.read();
//        Assert.assertEquals(in.read(), -1);
//        Assert.assertEquals(in.read(bytes), -1);
//        InputStream in2 = JieIO.limit(new ByteArrayInputStream(bytes), 6);
//        Assert.assertEquals(in2.read(bytes), 6);
//        Assert.assertEquals(in2.read(), -1);
//        Assert.assertEquals(in2.read(bytes), -1);
//        OutputStream out = JieIO.limit(new ByteArrayOutputStream(), 6);
//        Assert.expectThrows(IOException.class, () -> out.write(bytes));
//        out.write(bytes, 0, 6);
//        Assert.expectThrows(IOException.class, () -> out.write(1));
//    }
//
//    @Test
//    public void testWrapper() throws IOException {
//        String data = DATA;
//        byte[] dataBytes = data.getBytes(JieChars.defaultCharset());
//        File file = FileTest.createFile("IOTest-testWrapper.txt", data);
//        RandomAccessFile random = new RandomAccessFile(file, "rws");
//
//        testInputStream(data, 3, 222, JieIO.toInputStream(dataBytes, 3, 222), true);
//        testInputStream(data, 0, dataBytes.length, JieIO.toInputStream(ByteBuffer.wrap(data.getBytes(JieChars.defaultCharset()))), true);
//        testInputStream(data, 0, dataBytes.length, JieIO.toInputStream(new StringReader(DATA)), false);
//        testInputStream(data, 0, dataBytes.length, JieIO.toInputStream(random), true);
//        testInputStream(data, 2, 131, JieIO.toInputStream(random, 2, 131), true);
//        testInputStream(data, 2, 131, JieIO.limit(JieIO.toInputStream(dataBytes, 2, 131), 131), false);
//        testReader(data, 5, 155, new StringReader(data.substring(5, 5 + 155)), true);
//        testReader(data, 0, data.length(), JieIO.toReader(CharBuffer.wrap(DATA)), true);
//        testReader(data, 0, data.length(), JieIO.toReader(new ByteArrayInputStream(DATA.getBytes(JieChars.defaultCharset()))), false);
//
//        byte[] bytes = new byte[1024];
//        char[] chars = new char[1024];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
//        StringBuilder sb = new StringBuilder();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
//        testOutStream(bytes.length, JieIO.toOutputStream(buffer), (off, len) ->
//            Arrays.copyOfRange(bytes, off, off + len));
//        testOutStream(bytes.length, JieIO.limit(outputStream, 1024), (off, len) ->
//            Arrays.copyOfRange(outputStream.toByteArray(), off, off + len));
//        testOutStream(-1, JieIO.toOutputStream(sb), (off, len) ->
//            Arrays.copyOfRange(sb.toString().getBytes(JieChars.defaultCharset()), off, off + len));
//        testOutStream(-1, JieIO.toOutputStream(random), (off, len) ->
//            JieFile.readBytes(file.toPath(), off, len));
//        testOutStream(188, JieIO.toOutputStream(random, 8, 188), (off, len) ->
//            JieFile.readBytes(file.toPath(), off + 8, len));
//        testOutStream(-1, JieIO.toOutputStream(random, 8, -1), (off, len) ->
//            JieFile.readBytes(file.toPath(), off + 8, len));
//        byte[] back = new byte[2048];
//        testOutStream(bytes.length, JieIO.toOutputStream(back, 111, 1024), (off, len) ->
//            Arrays.copyOfRange(back, off + 111, off + 111 + len));
//        testWriter(JieIO.toWriter(CharBuffer.wrap(chars)), (off, len) ->
//            Arrays.copyOfRange(chars, off, off + len));
//        outputStream.reset();
//        testWriter(JieIO.toWriter(outputStream), (off, len) ->
//            new String(outputStream.toByteArray(), JieChars.defaultCharset()).substring(off, off + len).toCharArray());
//        random.close();
//        file.delete();
//    }
//
//    @Test
//    public void testTransform() throws IOException {
//        byte[] bytes = {1, 2, 3, 4, 5, 6, 7, 8, 8, 7, 6, 5, 4, 3, 2, 1, 1, 2, 3, 4};
//        ByteArrayInputStream source = new ByteArrayInputStream(bytes);
//        source.mark(0);
//        InputStream trans = JieIO.transform(source, 8, bs -> Arrays.copyOf(bs, bs.length / 2));
//        Assert.assertEquals(JieIO.read(trans), new byte[]{1, 2, 3, 4, 8, 7, 6, 5, 1, 2});
//        source.reset();
//        trans = JieIO.transform(source, 8, bs -> Arrays.copyOf(bs, bs.length / 2));
//        Assert.assertEquals(trans.skip(5), 5);
//        Assert.assertEquals(trans.read(), 7);
//        Assert.assertEquals(JieIO.read(trans), new byte[]{6, 5, 1, 2});
//    }
//
//    @Test
//    public void testSimpleBuffer() {
//        byte[] bytes = TestUtil.buildRandomBytes(111);
//        Assert.assertTrue(JieIO.isSimpleWrapper(ByteBuffer.wrap(bytes)));
//        Assert.assertFalse(JieIO.isSimpleWrapper(ByteBuffer.wrap(bytes, 0, 1)));
//        Assert.assertFalse(JieIO.isSimpleWrapper(ByteBuffer.wrap(bytes, 1, 1)));
//        Assert.assertFalse(JieIO.isSimpleWrapper(ByteBuffer.wrap(bytes).get(new byte[5])));
//        Assert.assertSame(JieIO.readBack(ByteBuffer.wrap(bytes)), bytes);
//        Assert.assertEquals(JieIO.readBack(ByteBuffer.wrap(bytes, 0, 1)), Arrays.copyOf(bytes, 1));
//    }
//
//    @Test
//    public void oldTestWrap() throws IOException {
//        String base = DATA;
//        StringBuilder text = new StringBuilder();
//        Random random = new Random();
//        for (int i = 0; i < 4096; i++) {
//            text.append(base.charAt(random.nextInt(base.length())));
//        }
//        String string = text.toString();
//        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
//        InputStream in = JieIO.toInputStream(new StringReader(string), StandardCharsets.UTF_8);
//        Assert.assertEquals(JieIO.readString(in), string);
//
//        InputStream in2 = JieIO.toInputStream(new StringReader(string), StandardCharsets.UTF_8);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        while (true) {
//            int b = in2.read();
//            if (b != -1) {
//                out.write(b);
//            } else {
//                break;
//            }
//        }
//        Assert.assertEquals(new String(out.toByteArray(), StandardCharsets.UTF_8), string);
//
//        StringWriter sw = new StringWriter();
//        OutputStream wo = JieIO.toOutputStream(sw, StandardCharsets.UTF_8);
//        wo.write(bytes);
//        wo.close();
//        Assert.assertEquals(sw.toString(), string);
//        StringWriter sw2 = new StringWriter();
//        OutputStream wo2 = JieIO.toOutputStream(sw2, StandardCharsets.UTF_8);
//        for (byte aByte : bytes) {
//            wo2.write(aByte);
//        }
//        wo2.close();
//        Assert.assertEquals(sw2.toString(), string);
//
//        char[] chars = new char[string.length()];
//        CharBuffer charBuffer = CharBuffer.wrap(chars);
//        JieIO.toWriter(charBuffer).write(string.toCharArray());
//        Assert.assertEquals(chars, string.toCharArray());
//        byte[] bytes2 = new byte[bytes.length];
//        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes2);
//        JieIO.toOutputStream(byteBuffer).write(bytes);
//        Assert.assertEquals(bytes2, bytes);
//    }
//
//    @Test
//    public void testReadFile() throws IOException {
//        String data = DATA;
//        File file = FileTest.createFile("IOTest-testReadFile.txt", data);
//        Assert.assertEquals(JieFile.readString(file.toPath()), data);
//        Assert.assertEquals(
//            JieFile.readString(file.toPath(), 18, 36),
//            new String(data.getBytes(JieChars.defaultCharset()), 18, 36)
//        );
//        file.delete();
//    }
//
//    @Test
//    public void testWriteFile() throws IOException {
//        File file = FileTest.createFile("IOTest-testWriteFile.txt");
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        JieFile.writeString(file.toPath(), "lalala");
//        JieFile.writeString(file.toPath(), 6, 18, "222");
//        Assert.assertEquals(JieFile.readString(file.toPath()), "lalala222");
//        JieFile.writeString(file.toPath(), 6, 7, "1");
//        Assert.assertEquals(JieFile.readString(file.toPath()), "lalala122");
//        JieFile.writeString(file.toPath(), 7, 100, "3333中文中文");
//        Assert.assertEquals(JieFile.readString(file.toPath()), "lalala13333中文中文");
//        file.delete();
//    }
//
//    private static final class TestInput extends InputStream {
//
//        private final InputStream in;
//
//        private TestInput(InputStream in) {
//            this.in = in;
//        }
//
//        @Override
//        public int read() throws IOException {
//            return in.read();
//        }
//
//        @Override
//        public int read(byte[] b) throws IOException {
//            return in.read(b);
//        }
//
//        @Override
//        public int read(byte[] b, int off, int len) throws IOException {
//            return in.read(b, off, len);
//        }
//
//        @Override
//        public long skip(long n) throws IOException {
//            return in.skip(n);
//        }
//
//        @Override
//        public int available() throws IOException {
//            int a = in.available();
//            if (a <= 0) {
//                return a;
//            }
//            return 1;
//        }
//
//        @Override
//        public void close() throws IOException {
//            in.close();
//        }
//
//        @Override
//        public void mark(int readlimit) {
//            in.mark(readlimit);
//        }
//
//        @Override
//        public void reset() throws IOException {
//            in.reset();
//        }
//
//        @Override
//        public boolean markSupported() {
//            return in.markSupported();
//        }
//    }
//}
