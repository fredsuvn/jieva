package test.io;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.JieRandom;
import xyz.fslabo.common.io.AbstractWriter;
import xyz.fslabo.common.io.IORuntimeException;
import xyz.fslabo.common.io.JieIO;
import xyz.fslabo.test.JieTest;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.testng.Assert.*;

public class StreamTest {

    public static final int SOURCE_SIZE = 1024;

    @Test
    public void testInput() throws Exception {
        byte[] source = new byte[SOURCE_SIZE];
        JieRandom.fill(source);

        // bytes
        testInput(JieIO.wrapIn(source), source, true);
        testInput(
            JieIO.wrapIn(source, 2, source.length - 10),
            Arrays.copyOfRange(source, 2, source.length - 8),
            true
        );

        // buffer
        ByteBuffer buffer = ByteBuffer.wrap(source);
        InputStream bufferIn = JieIO.wrapIn(buffer);
        testInput(bufferIn, source, true);
        Class<?> bufferInClass = bufferIn.getClass();
        Method read0 = bufferInClass.getDeclaredMethod("read0");
        JieTest.testThrow(IOException.class, read0, bufferIn);
        read0 = bufferInClass.getDeclaredMethod("read0", byte[].class, int.class, int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, null, 0, 0);
        read0 = bufferInClass.getDeclaredMethod("skip0", int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, 99);

        // file
        Path path = Paths.get("src", "test", "resources", "io", "input.test");
        RandomAccessFile raf = new FakeRandomFile(path.toFile(), "r", source);
        InputStream rafIn = JieIO.wrapIn(raf, 6);
        testInput(rafIn, Arrays.copyOfRange(source, 6, source.length), true);
        rafIn.close();
        expectThrows(IORuntimeException.class, () -> rafIn.mark(66));

        // chars
        char[] chars = JieRandom.fill(new char[1024], '0', '9');
        byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        InputStream charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        testInput(charsIn, charBytes, false);
        charsIn.close();
        expectThrows(IOException.class, charsIn::read);
        chars = JieRandom.fill(new char[1024], '\u4e00', '\u9fff');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        testInput(charsIn, charBytes, false);
        chars = new char[1024];
        // '0'
        Arrays.fill(chars, '0');
        charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        charsIn.read(new byte[1024]);
        // emoji: "\uD83D\uDD1E"
        for (int i = 0; i < chars.length; i += 2) {
            chars[i] = '\uD83D';
            chars[i + 1] = '\uDD1E';
        }
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        testInput(charsIn, charBytes, false);
        // error: U+DD88
        Arrays.fill(chars, '\uDD88');
        charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        expectThrows(IOException.class, charsIn::read);

        // error
        FakeRandomFile.SEEK_ERR = true;
        expectThrows(IORuntimeException.class, () -> JieIO.wrapIn(raf, 6));
        FakeRandomFile.SEEK_ERR = false;
        expectThrows(NullPointerException.class, () -> JieIO.wrapIn(null, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapIn(source, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapIn(source, -2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapIn(source, 2, -(source.length + 1)));
    }

    private void testInput(InputStream in, byte[] source, boolean available) throws Exception {
        byte[] dest = new byte[source.length];
        assertEquals(in.read(dest, 0, 0), 0);
        assertEquals((byte) in.read(), source[0]);
        assertEquals((byte) in.read(), source[1]);
        assertEquals((byte) in.read(), source[2]);
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 3, 13));
        if (available) {
            assertEquals(in.available(), source.length - 13);
        } else {
            assertTrue(in.available() <= source.length - 13 && in.available() >= 0);
        }
        if (in.markSupported()) {
            expectThrows(IOException.class, in::reset);
            in.mark(999);
            assertEquals(in.read(dest, 3, 10), 10);
            assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
            in.reset();
        }
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(source.length), source.length - 23);
        assertEquals(in.read(), -1);
        assertEquals(in.read(dest, 3, 10), -1);
        assertEquals(in.skip(source.length), 0);

        in.close();
    }

    @Test
    public void testOutput() throws Exception {
        byte[] source = new byte[SOURCE_SIZE];
        Arrays.fill(source, (byte) 1);

        // bytes
        OutputStream byteOut = JieIO.wrapOut(source);
        byte[] data = JieRandom.fill(new byte[source.length]);
        testOutput(byteOut, data);
        assertEquals(data, source);
        Arrays.fill(source, (byte) 1);
        data = JieRandom.fill(new byte[source.length - 10]);
        OutputStream byteOut2 = JieIO.wrapOut(source, 2, data.length);
        testOutput(byteOut2, data);
        assertEquals(data, Arrays.copyOfRange(source, 2, data.length + 2));

        // buffer
        OutputStream bufferOut = JieIO.wrapOut(ByteBuffer.wrap(source));
        data = JieRandom.fill(new byte[source.length]);
        testOutput(bufferOut, data);
        assertEquals(data, source);

        // file
        Path path = Paths.get("src", "test", "resources", "io", "input.test");
        RandomAccessFile raf = new FakeRandomFile(path.toFile(), "r", source);
        OutputStream rafOut = JieIO.wrapOut(raf, 6);
        data = JieRandom.fill(new byte[source.length - 6]);
        testOutput(rafOut, data);
        assertEquals(data, Arrays.copyOfRange(source, 6, data.length + 6));
        rafOut.flush();
        rafOut.close();
        expectThrows(IOException.class, () -> rafOut.write(1));

        // chars
        // char[] chars = JieRandom.fill(new char[1024], '0', '9');
        // byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // OutputStream charsOut = JieIO.wrapOutputStream(new CharArrayWriter());
        // testOutput(charsOut, charBytes);
        // charsIn.close();
        // expectThrows(IOException.class, charsIn::read);
        // chars = JieRandom.fill(new char[1024], '\u4e00', '\u9fff');
        // charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // testInput(charsIn, charBytes, false);
        // chars = new char[1024];
        // // '0'
        // Arrays.fill(chars, '0');
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // charsIn.read(new byte[1024]);
        // // emoji: "\uD83D\uDD1E"
        // for (int i = 0; i < chars.length; i += 2) {
        //     chars[i] = '\uD83D';
        //     chars[i + 1] = '\uDD1E';
        // }
        // charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // testInput(charsIn, charBytes, false);
        // // error: U+DD88
        // Arrays.fill(chars, '\uDD88');
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // expectThrows(IOException.class, charsIn::read);


        // error
        FakeRandomFile.SEEK_ERR = true;
        expectThrows(IORuntimeException.class, () -> JieIO.wrapOut(raf, 6));
        FakeRandomFile.SEEK_ERR = false;
        expectThrows(NullPointerException.class, () -> JieIO.wrapOut(null, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapOut(source, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapOut(source, -2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapOut(source, 2, -(source.length + 1)));
    }

    private void testOutput(OutputStream out, byte[] data) throws Exception {
        out.write(data[0]);
        out.write(data[1]);
        out.write(Arrays.copyOfRange(data, 2, data.length));
        out.write(Arrays.copyOfRange(data, 2, 2));

        expectThrows(IOException.class, () -> out.write(1));
        expectThrows(IOException.class, () -> out.write(new byte[10]));

        out.flush();
        out.close();
    }

    @Test
    public void testReader() throws Exception {
        char[] source = new char[SOURCE_SIZE];
        JieRandom.fill(source);

        // chars
        testReader(JieIO.wrapReader(source), source);
        testReader(
            JieIO.wrapReader(source, 2, source.length - 10),
            Arrays.copyOfRange(source, 2, source.length - 8)
        );

        // string
        testReader(JieIO.wrapReader(new String(source)), source);

        // buffer
        CharBuffer buffer = CharBuffer.wrap(source);
        Reader bufferIn = JieIO.wrapReader(buffer);
        testReader(bufferIn, source);
        Class<?> bufferInClass = bufferIn.getClass();
        Method read0 = bufferInClass.getDeclaredMethod("read0");
        JieTest.testThrow(IOException.class, read0, bufferIn);
        read0 = bufferInClass.getDeclaredMethod("read0", char[].class, int.class, int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, null, 0, 0);
        read0 = bufferInClass.getDeclaredMethod("skip0", int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, 99);

        // chars
        // char[] chars = JieRandom.fill(new char[1024], '0', '9');
        // byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // InputStream charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        // testInput(charsIn, charBytes, false);
        // charsIn.close();
        // expectThrows(IOException.class, charsIn::read);
        // chars = JieRandom.fill(new char[1024], '\u4e00', '\u9fff');
        // charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        // testInput(charsIn, charBytes, false);
        // chars = new char[1024];
        // // '0'
        // Arrays.fill(chars, '0');
        // charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        // charsIn.read(new byte[1024]);
        // // emoji: "\uD83D\uDD1E"
        // for (int i = 0; i < chars.length; i += 2) {
        //     chars[i] = '\uD83D';
        //     chars[i + 1] = '\uDD1E';
        // }
        // charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        // testInput(charsIn, charBytes, false);
        // // error: U+DD88
        // Arrays.fill(chars, '\uDD88');
        // charsIn = JieIO.wrapIn(new CharArrayReader(chars));
        // expectThrows(IOException.class, charsIn::read);

        // error
        expectThrows(NullPointerException.class, () -> JieIO.wrapReader(null, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapReader(source, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapReader(source, -2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapReader(source, 2, -(source.length + 1)));
    }

    private void testReader(Reader in, char[] source) throws Exception {
        char[] dest = new char[source.length];
        assertTrue(in.ready());
        assertEquals(in.read(dest, 0, 0), 0);
        assertEquals((char) in.read(), source[0]);
        assertEquals((char) in.read(), source[1]);
        CharBuffer target = CharBuffer.allocate(1);
        assertEquals(in.read(target), 1);
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 3, 13));
        if (in.markSupported()) {
            expectThrows(IOException.class, in::reset);
            in.mark(999);
            assertEquals(in.read(dest, 3, 10), 10);
            assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
            in.reset();
        }
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(source.length), source.length - 23);
        assertEquals(in.read(), -1);
        assertEquals(in.read(dest, 3, 10), -1);
        assertEquals(in.skip(source.length), 0);

        in.close();
    }

    @Test
    public void testWriter() throws Exception {
        char[] source = new char[SOURCE_SIZE];
        Arrays.fill(source, (char) 1);

        // chars
        Writer charsOut = JieIO.wrapWriter(source);
        char[] data = JieRandom.fill(new char[source.length]);
        testWriter(charsOut, data);
        assertEquals(data, source);
        Arrays.fill(source, (char) 1);
        data = JieRandom.fill(new char[source.length - 10]);
        Writer charsOut2 = JieIO.wrapWriter(source, 2, data.length);
        testWriter(charsOut2, data);
        assertEquals(data, Arrays.copyOfRange(source, 2, data.length + 2));
        char[] nullChars = new char[4];
        Writer nullWriter = JieIO.wrapWriter(nullChars);
        nullWriter.append(null);
        nullWriter.write("null", 0, 0);
        assertEquals(nullChars, "null".toCharArray());

        // buffer
        Writer bufferOut = JieIO.wrapWriter(CharBuffer.wrap(source));
        data = JieRandom.fill(new char[source.length]);
        testWriter(bufferOut, data);
        assertEquals(data, source);

        // abstract
        Writer absOut = new AbstractWriter() {

            @Override
            protected void doWrite(char c) {
            }

            @Override
            protected void doWrite(char[] cbuf, int off, int len) {
            }

            @Override
            protected void doWrite(String str, int off, int len) {
                throw new IllegalStateException();
            }

            @Override
            protected void doAppend(CharSequence csq, int start, int end) {
                throw new IllegalStateException();
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
        };
        expectThrows(IOException.class, () -> absOut.append("123"));
        expectThrows(IOException.class, () -> absOut.write("123"));

        // chars
        // char[] chars = JieRandom.fill(new char[1024], '0', '9');
        // byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // OutputStream charsOut = JieIO.wrapOutputStream(new CharArrayWriter());
        // testOutput(charsOut, charBytes);
        // charsIn.close();
        // expectThrows(IOException.class, charsIn::read);
        // chars = JieRandom.fill(new char[1024], '\u4e00', '\u9fff');
        // charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // testInput(charsIn, charBytes, false);
        // chars = new char[1024];
        // // '0'
        // Arrays.fill(chars, '0');
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // charsIn.read(new byte[1024]);
        // // emoji: "\uD83D\uDD1E"
        // for (int i = 0; i < chars.length; i += 2) {
        //     chars[i] = '\uD83D';
        //     chars[i + 1] = '\uDD1E';
        // }
        // charBytes = new String(chars).getBytes(JieChars.UTF_8);
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // testInput(charsIn, charBytes, false);
        // // error: U+DD88
        // Arrays.fill(chars, '\uDD88');
        // charsIn = JieIO.wrapInputStream(new CharArrayReader(chars));
        // expectThrows(IOException.class, charsIn::read);

        // error
        expectThrows(NullPointerException.class, () -> JieIO.wrapWriter(null, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapWriter(source, 2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapWriter(source, -2, source.length + 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.wrapWriter(source, 2, -(source.length + 1)));
    }

    private void testWriter(Writer out, char[] data) throws Exception {
        out.write(data[0]);
        out.append(data[1]);
        out.write(new String(data, 2, 1));
        out.write(Arrays.copyOfRange(data, 3, 10));
        out.append(new String(data, 10, 10));
        out.write(Arrays.copyOfRange(data, 20, data.length));
        out.write(new char[0]);
        out.append("");

        expectThrows(IOException.class, () -> out.write(1));
        expectThrows(IOException.class, () -> out.write(new char[10]));

        out.flush();
        out.close();
    }

    private static final class FakeRandomFile extends RandomAccessFile {

        private static boolean SEEK_ERR = false;

        private int seek = 0;
        private final byte[] data;
        private volatile InputStream in;
        private volatile OutputStream out;
        private boolean close = false;

        public FakeRandomFile(File name, String mode, byte[] data) throws IOException {
            super(name, mode);
            this.data = data;
        }

        private InputStream getIn() {
            if (in == null) {
                in = JieIO.wrapIn(data, seek, data.length - seek);
            }
            return in;
        }

        @Override
        public int read() throws IOException {
            checkClose();
            int result = getIn().read();
            if (result >= 0) {
                seek++;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            checkClose();
            int result = getIn().read(b, off, len);
            if (result >= 0) {
                seek += result;
            }
            return result;
        }

        @Override
        public long getFilePointer() throws IOException {
            checkClose();
            return seek;
        }

        @Override
        public void seek(long pos) throws IOException {
            if (SEEK_ERR) {
                throw new IOException();
            }
            checkClose();
            this.seek = (int) pos;
            this.in = null;
            this.out = null;
        }

        @Override
        public long length() throws IOException {
            checkClose();
            return data.length;
        }

        private OutputStream getOut() {
            if (out == null) {
                out = JieIO.wrapOut(data, seek, data.length - seek);
            }
            return out;
        }

        @Override
        public void write(int b) throws IOException {
            checkClose();
            getOut().write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkClose();
            getOut().write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            close = true;
        }

        private void checkClose() throws IOException {
            if (close) {
                throw new IOException("Stream closed.");
            }
        }
    }
}
