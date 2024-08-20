package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.GekBytes;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.JieCheck;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Input/Output utilities.
 *
 * @author fresduvn
 */
public class JieIO {

    /**
     * Default IO buffer size: 1024 * 8 = 8192.
     */
    public static final int IO_BUFFER_SIZE = 1024 * 8;

    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(GekBytes.emptyBytes());

    // Common IO methods:

    /**
     * Reads all bytes from source stream into an array.
     * Returns the array, or null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    @Nullable
    public static byte[] read(InputStream source) throws JieIOException {
        try {
            int available = source.available();
            ByteArrayOutputStream dest;
            boolean hasRead = false;
            if (available > 0) {
                byte[] bytes = new byte[available];
                int c = source.read(bytes);
                if (c == -1) {
                    return null;
                }
                if (c == available) {
                    int r = source.read();
                    if (r == -1) {
                        return bytes;
                    } else {
                        dest = new ByteArrayOutputStream(available + 1);
                        dest.write(bytes);
                        dest.write(r);
                        hasRead = true;
                    }
                } else {
                    dest = new ByteArrayOutputStream(c);
                    dest.write(bytes, 0, c);
                    hasRead = true;
                }
            } else {
                dest = new ByteArrayOutputStream();
            }
            long readCount = readTo(source, dest);
            if (readCount == -1) {
                if (hasRead) {
                    return dest.toByteArray();
                }
                return null;
            }
            return dest.toByteArray();
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads specified number of bytes from source stream into an array.
     * Returns the array, or null if no data read out and reaches to the end of stream.
     * <p>
     * If the number &lt; 0, read all as {@link #read(InputStream)};
     * els if the number is 0, no read and return an empty array;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the stream.
     *
     * @param source source stream
     * @param number specified number
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    @Nullable
    public static byte[] read(InputStream source, int number) throws JieIOException {
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new byte[0];
        }
        try {
            int b = source.read();
            if (b == -1) {
                return null;
            }
            byte[] dest = new byte[number];
            dest[0] = (byte) b;
            int remaining = number - 1;
            int offset = 1;
            while (remaining > 0) {
                int readSize = source.read(dest, offset, remaining);
                if (readSize < 0) {
                    return Arrays.copyOfRange(dest, 0, number - remaining);
                }
                remaining -= readSize;
                offset += readSize;
            }
            return dest;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads bytes from source stream into dest array,
     * returns actual read number, or -1 if no data read out and reaches to the end of stream.
     * This method will keep reading until the dest array is filled up,
     * or the reading reaches the end of the stream.
     *
     * @param source source stream
     * @param dest   dest array
     * @return actual read number, or -1 if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    public static int readTo(InputStream source, byte[] dest) {
        return readTo(source, dest, 0, dest.length);
    }

    /**
     * Reads bytes of specified length from source stream into dest array starting from given offset,
     * returns actual read number, or -1 if no data read out and reaches to the end of stream.
     * This method will keep reading until the read number reaches to the specified length,
     * or the reading reaches the end of the stream.
     *
     * @param source source stream
     * @param dest   dest array
     * @param offset given offset
     * @param length specified length
     * @return actual read number, or -1 if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    public static int readTo(InputStream source, byte[] dest, int offset, int length) throws JieIOException {
        try {
            JieCheck.checkRangeInBounds(offset, offset + length, 0, dest.length);
            if (length < 0) {
                throw new IllegalArgumentException("length < 0: " + length + ".");
            }
            if (length == 0) {
                return 0;
            }
            int remaining = length;
            int off = 0;
            while (remaining > 0) {
                int readSize = source.read(dest, off, remaining);
                if (readSize < 0) {
                    if (remaining == length) {
                        return -1;
                    }
                    return length - remaining;
                }
                remaining -= readSize;
                offset += readSize;
            }
            return length;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads bytes from source stream into dest buffer,
     * returns actual read number, or -1 if no data read out and reaches to the end of stream.
     * This method will keep reading until the dest buffer is filled up,
     * or the reading reaches the end of the stream.
     *
     * @param source source stream
     * @param dest   dest buffer
     * @return actual read number, or -1 if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    public static int readTo(InputStream source, ByteBuffer dest) {
        try {
            if (dest.remaining() <= 0) {
                return 0;
            }
            if (dest.hasArray()) {
                int readSize = readTo(source, dest.array(), dest.arrayOffset() + dest.position(), dest.remaining());
                if (readSize > 0) {
                    dest.position(dest.position() + readSize);
                }
                return readSize;
            }
            byte[] bytes = read(source, dest.remaining());
            if (bytes == null) {
                return -1;
            }
            if (bytes.length == 0) {
                return 0;
            }
            dest.put(bytes);
            return bytes.length;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads all bytes from source stream into dest stream,
     * returns actual read number, or -1 if no data read out and reaches to the end of source stream.
     * This method will keep reading until the reading reaches the end of source stream.
     *
     * @param source source stream
     * @param dest   dest stream
     * @return actual read number, or -1 if no data read out and reaches to the end of source stream
     * @throws JieIOException IO exception
     */
    public static long readTo(InputStream source, OutputStream dest) throws JieIOException {
        return readTo(source, dest, -1);
    }

    /**
     * Reads specified number of bytes from source stream into dest stream,
     * returns actual read number, or -1 if no data read out and reaches to the end of source stream.
     * <p>
     * If the number &lt; 0, read all;
     * els if the number is 0, no read and return 0;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the stream.
     *
     * @param source source stream
     * @param dest   dest stream
     * @param number specified number
     * @return actual read number, or -1 if no data read out and reaches to the end of source stream
     * @throws JieIOException IO exception
     */
    public static long readTo(InputStream source, OutputStream dest, long number) throws JieIOException {
        return readTo(source, dest, number, IO_BUFFER_SIZE);
    }

    /**
     * Reads specified number of bytes from source stream into dest stream,
     * returns actual read number, or -1 if no data read out and reaches to the end of source stream.
     * <p>
     * If the number &lt; 0, read all;
     * els if the number is 0, no read and return 0;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the stream.
     *
     * @param source     source stream
     * @param dest       dest stream
     * @param number     specified number
     * @param bufferSize buffer size for each IO operation
     * @return actual read number, or -1 if no data read out and reaches to the end of source stream
     * @throws JieIOException IO exception
     */
    public static long readTo(InputStream source, OutputStream dest, long number, int bufferSize) throws JieIOException {
        try {
            if (bufferSize <= 0) {
                throw new IllegalArgumentException("bufferSize <= 0.");
            }
            if (number == 0) {
                return 0;
            }
            long readNum = 0;
            int bufSize = number < 0 ? bufferSize : (int) Math.min(number, bufferSize);
            byte[] buffer = new byte[bufSize];
            while (true) {
                int tryReadLen = number < 0 ? buffer.length : (int) Math.min(number - readNum, buffer.length);
                int readSize = source.read(buffer, 0, tryReadLen);
                if (readSize < 0) {
                    if (readNum == 0) {
                        return -1;
                    }
                    break;
                }
                if (readSize > 0) {
                    dest.write(buffer, 0, readSize);
                    readNum += readSize;
                }
                if (number < 0) {
                    continue;
                }
                if (number - readNum <= 0) {
                    break;
                }
            }
            return readNum;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads available bytes from source stream into an array.
     * Returns the array, or null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    @Nullable
    public static byte[] available(InputStream source) throws JieIOException {
        try {
            int available = source.available();
            if (available > 0) {
                byte[] bytes = new byte[available];
                int c = source.read(bytes);
                if (c == -1) {
                    return null;
                }
                if (c == available) {
                    return bytes;
                }
                return Arrays.copyOf(bytes, c);
            }
            if (available == 0) {
                byte[] b = new byte[1];
                int readSize = source.read(b);
                if (readSize == -1) {
                    return null;
                }
                if (readSize == 0) {
                    return new byte[0];
                }
                return b;
            }
            return null;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads available bytes from source stream into dest stream,
     * returns actual read number, or -1 if no data read out and reaches to the end of source stream.
     *
     * @param source source stream
     * @param dest   dest stream
     * @return actual read number, or -1 if no data read out and reaches to the end of source stream
     * @throws JieIOException IO exception
     */
    public static long availableTo(InputStream source, OutputStream dest) throws JieIOException {
        try {
            byte[] available = available(source);
            if (available == null) {
                return -1;
            }
            if (available.length == 0) {
                return 0;
            }
            dest.write(available);
            return available.length;
        } catch (IOException e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads all chars from source reader into a string.
     * Returns the string, or null if no data read out and reaches to the end of reader.
     *
     * @param source source reader
     * @return the string, or null if no data read out and reaches to the end of reader
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String read(Reader source) throws JieIOException {
        try {
            StringBuilder dest = new StringBuilder();
            long readCount = readTo(source, dest);
            if (readCount == -1) {
                return null;
            }
            return dest.toString();
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads specified number of chars from source reader into a string.
     * Returns the string, or null if no data read out and reaches to the end of reader.
     * <p>
     * If the number &lt; 0, read all as {@link #read(Reader)};
     * els if the number is 0, no read and return an empty array;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the reader.
     *
     * @param source source reader
     * @param number specified number
     * @return the string, or null if no data read out and reaches to the end of reader
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String read(Reader source, int number) throws JieIOException {
        try {
            StringBuilder dest = new StringBuilder();
            long readCount = readTo(source, dest, number);
            if (readCount == -1) {
                return null;
            }
            return dest.toString();
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads chars from source reader into dest appendable,
     * returns actual read number, or -1 if no data read out and reaches to the end of reader.
     * This method will keep reading until the reading reaches the end of source reader.
     *
     * @param source source reader
     * @param dest   dest appendable
     * @return actual read number, or -1 if no data read out and reaches to the end of reader
     * @throws JieIOException IO exception
     */
    public static long readTo(Reader source, Appendable dest) throws JieIOException {
        if (dest instanceof CharBuffer) {
            try {
                return source.read((CharBuffer) dest);
            } catch (IOException e) {
                throw new JieIOException(e);
            }
        }
        return readTo(source, dest, -1);
    }

    /**
     * Reads specified number of chars from source reader into dest appendable,
     * returns actual read number, or -1 if no data read out and reaches to the end of source reader.
     * <p>
     * If the number &lt; 0, read all;
     * els if the number is 0, no read and return 0;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the reader.
     *
     * @param source source reader
     * @param dest   dest appendable
     * @param number specified number
     * @return actual read number, or -1 if no data read out and reaches to the end of source reader
     * @throws JieIOException IO exception
     */
    public static long readTo(Reader source, Appendable dest, int number) throws JieIOException {
        return readTo(source, dest, number, IO_BUFFER_SIZE);
    }

    /**
     * Reads specified number of chars from source reader into dest appendable,
     * returns actual read number, or -1 if no data read out and reaches to the end of source reader.
     * <p>
     * If the number &lt; 0, read all;
     * els if the number is 0, no read and return 0;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the reader.
     *
     * @param source     source reader
     * @param dest       dest appendable
     * @param number     specified number
     * @param bufferSize buffer size for each IO operation
     * @return actual read number, or -1 if no data read out and reaches to the end of source reader
     * @throws JieIOException IO exception
     */
    public static long readTo(Reader source, Appendable dest, int number, int bufferSize) throws JieIOException {
        try {
            if (bufferSize <= 0) {
                throw new IllegalArgumentException("bufferSize <= 0.");
            }
            if (number == 0) {
                return 0;
            }
            long readNum = 0;
            int bufSize = number < 0 ? bufferSize : Math.min(number, bufferSize);
            char[] buffer = new char[bufSize];
            while (true) {
                int tryReadLen = number < 0 ? buffer.length : (int) Math.min(number - readNum, buffer.length);
                int readSize = source.read(buffer, 0, tryReadLen);
                if (readSize < 0) {
                    if (readNum == 0) {
                        return -1;
                    }
                    break;
                }
                if (readSize > 0) {
                    append(dest, buffer, 0, readSize);
                    readNum += readSize;
                }
                if (number < 0) {
                    continue;
                }
                if (number - readNum <= 0) {
                    break;
                }
            }
            return readNum;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    private static void append(Appendable dest, char[] chars, int off, int len) throws IOException {
        if (dest instanceof StringBuilder) {
            ((StringBuilder) dest).append(chars, off, len);
        } else if (dest instanceof StringBuffer) {
            ((StringBuffer) dest).append(chars, off, len);
        } else if (dest instanceof Writer) {
            ((Writer) dest).write(chars, off, len);
        } else if (dest instanceof CharBuffer) {
            ((CharBuffer) dest).put(chars, off, len);
        } else {
            dest.append(new String(chars, off, len));
        }
    }

    /**
     * Reads all bytes from source stream into a string with {@link JieChars#defaultCharset()}.
     * Returns the string, or null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String readString(InputStream source) throws JieIOException {
        return readString(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from source stream into a string with specified charset.
     * Returns the string, or null if no data read out and reaches to the end of stream.
     *
     * @param source  source stream
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String readString(InputStream source, Charset charset) throws JieIOException {
        try {
            byte[] bytes = read(source);
            if (bytes == null) {
                return null;
            }
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads available bytes from source stream into a string with {@link JieChars#defaultCharset()}.
     * Returns the string, or null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String avalaibleString(InputStream source) throws JieIOException {
        return avalaibleString(source, JieChars.defaultCharset());
    }

    /**
     * Reads available bytes from source stream into a string with specified charset.
     * Returns the string, or null if no data read out and reaches to the end of stream.
     *
     * @param source  source stream
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String avalaibleString(InputStream source, Charset charset) throws JieIOException {
        try {
            byte[] bytes = available(source);
            if (bytes == null) {
                return null;
            }
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Wraps given stream as {@link Reader} with {@link JieChars#defaultCharset()}.
     *
     * @param stream given stream
     * @return given stream as {@link Reader}
     * @throws JieIOException IO exception
     */
    public static Reader toReader(InputStream stream) throws JieIOException {
        return toReader(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps given stream as {@link Reader} with specified charset.
     *
     * @param stream  given stream
     * @param charset specified charset
     * @return given stream as {@link Reader}
     * @throws JieIOException IO exception
     */
    public static Reader toReader(InputStream stream, Charset charset) throws JieIOException {
        return new InputStreamReader(stream, charset);
    }

    /**
     * Wraps given buffer as {@link Reader}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link Reader}
     * @throws JieIOException IO exception
     */
    public static Reader toReader(CharBuffer buffer) throws JieIOException {
        return new CharBufferReader(buffer);
    }

    /**
     * Wraps given reader as {@link InputStream} with {@link JieChars#defaultCharset()}.
     * The returned stream doesn't support mark/reset methods.
     *
     * @param reader given reader
     * @return given reader as {@link InputStream}
     * @throws JieIOException IO exception
     */
    public static InputStream toInputStream(Reader reader) throws JieIOException {
        return toInputStream(reader, JieChars.defaultCharset());
    }

    /**
     * Wraps given reader as {@link InputStream} with specified charset.
     * The returned stream doesn't support mark/reset methods.
     *
     * @param reader  given reader
     * @param charset specified charset
     * @return given reader as {@link InputStream}
     * @throws JieIOException IO exception
     */
    public static InputStream toInputStream(Reader reader, Charset charset) throws JieIOException {
        return new ReaderInputStream(reader, charset);
    }

    /**
     * Wraps given array as {@link ByteArrayInputStream}.
     *
     * @param array given array
     * @return given array as {@link ByteArrayInputStream}
     * @throws JieIOException IO exception
     */
    public static ByteArrayInputStream toInputStream(byte[] array) throws JieIOException {
        return new ByteArrayInputStream(array);
    }

    /**
     * Wraps given array as {@link ByteArrayInputStream}, starting from given offset with specified length.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return given array as {@link ByteArrayInputStream}
     * @throws JieIOException IO exception
     */
    public static ByteArrayInputStream toInputStream(byte[] array, int offset, int length) throws JieIOException {
        return new ByteArrayInputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as {@link InputStream}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link InputStream}
     * @throws JieIOException IO exception
     */
    public static InputStream toInputStream(ByteBuffer buffer) throws JieIOException {
        return new ByteBufferInputStream(buffer);
    }

    /**
     * Wraps given stream as {@link Writer} with {@link JieChars#defaultCharset()}.
     *
     * @param stream given stream
     * @return given stream as {@link Writer}
     * @throws JieIOException IO exception
     */
    public static Writer toWriter(OutputStream stream) throws JieIOException {
        return toWriter(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps given stream as {@link Writer} with {@link JieChars#defaultCharset()}.
     *
     * @param stream  given stream
     * @param charset specified charset
     * @return given stream as {@link Writer}
     * @throws JieIOException IO exception
     */
    public static Writer toWriter(OutputStream stream, Charset charset) throws JieIOException {
        return new OutputStreamWriter(stream, charset);
    }

    /**
     * Wraps given buffer as {@link Writer}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link Writer}
     * @throws JieIOException IO exception
     */
    public static Writer toWriter(CharBuffer buffer) throws JieIOException {
        return new CharBufferWriter(buffer);
    }

    /**
     * Wraps given appendable as {@link OutputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Note {@link OutputStream#flush()} and {@link OutputStream#close()} are valid
     * if given appendable is instance of {@link Writer}.
     *
     * @param appendable given appendable
     * @return given appendable as {@link OutputStream}
     * @throws JieIOException IO exception
     */
    public static OutputStream toOutputStream(Appendable appendable) throws JieIOException {
        return toOutputStream(appendable, JieChars.defaultCharset());
    }

    /**
     * Wraps given appendable as {@link OutputStream} with specified charset.
     * <p>
     * Note {@link OutputStream#flush()} and {@link OutputStream#close()} are valid
     * if given appendable is instance of {@link Writer}.
     *
     * @param appendable given appendable
     * @param charset    specified charset
     * @return given appendable as {@link OutputStream}
     * @throws JieIOException IO exception
     */
    public static OutputStream toOutputStream(Appendable appendable, Charset charset) throws JieIOException {
        return new AppendableOutputStream(appendable, charset);
    }

    /**
     * Wraps given array as {@link OutputStream}.
     *
     * @param array given array
     * @return given array as {@link OutputStream}
     * @throws JieIOException IO exception
     */
    public static OutputStream toOutputStream(byte[] array) throws JieIOException {
        return new ByteArrayAsOutputStream(array, 0, array.length);
    }

    /**
     * Wraps given array as {@link OutputStream}, starting from given offset with specified length.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return given array as {@link OutputStream}
     * @throws JieIOException IO exception
     */
    public static OutputStream toOutputStream(byte[] array, int offset, int length) throws JieIOException {
        JieCheck.checkRangeInBounds(offset, offset + length, 0, array.length);
        return new ByteArrayAsOutputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as {@link OutputStream}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link OutputStream}
     * @throws JieIOException IO exception
     */
    public static OutputStream toOutputStream(ByteBuffer buffer) throws JieIOException {
        return new ByteBufferOutputStream(buffer);
    }

    /**
     * Limits given stream to a specified number for readable bytes.
     * The returned stream doesn't support mark/reset methods.
     *
     * @param stream given stream
     * @param number number of readable bytes, must &gt;= 0
     * @return limited {@link InputStream}
     * @throws JieIOException IO exception
     */
    public static InputStream limit(InputStream stream, long number) throws JieIOException {
        if (number < 0) {
            throw new IllegalArgumentException("number < 0.");
        }
        return new LimitedInputStream(stream, number);
    }

    /**
     * Limits given stream to a specified number for writeable bytes.
     *
     * @param stream given stream
     * @param number number of writeable bytes, must &gt;= 0
     * @return limited {@link OutputStream}
     * @throws JieIOException IO exception
     */
    public static OutputStream limit(OutputStream stream, long number) throws JieIOException {
        if (number < 0) {
            throw new IllegalArgumentException("number < 0.");
        }
        return new LimitedOutputStream(stream, number);
    }

    /**
     * Returns a {@link TransformInputStream} from source stream, block size and transformer.
     * See {@link TransformInputStream} and
     * {@link TransformInputStream#TransformInputStream(InputStream, int, Function)}.
     *
     * @param source      source stream
     * @param blockSize   block size
     * @param transformer given transformer
     * @return a {@link TransformInputStream}
     * @see TransformInputStream
     * @see TransformInputStream#TransformInputStream(InputStream, int, Function)
     */
    public static TransformInputStream transform(
        InputStream source, int blockSize, Function<byte[], byte[]> transformer) {
        return new TransformInputStream(source, blockSize, transformer);
    }

    // Buffer methods:

    /**
     * Returns an empty byte buffer.
     *
     * @return an empty byte buffer
     */
    public static ByteBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Reads all bytes from source buffer into an array.
     * Returns the array, or null if no data read out and reaches to the end of buffer.
     *
     * @param source source buffer
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    @Nullable
    public static byte[] read(ByteBuffer source) throws JieIOException {
        try {
            int length = source.remaining();
            if (length <= 0) {
                return null;
            }
            byte[] result = new byte[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads specified number of bytes from source buffer into an array.
     * Returns the array, or null if no data read out and reaches to the end of buffer.
     * <p>
     * If the number &lt; 0, read all as {@link #read(ByteBuffer)};
     * els if the number is 0, no read and return an empty array;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the buffer.
     *
     * @param source source buffer
     * @param number specified number
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    @Nullable
    public static byte[] read(ByteBuffer source, int number) throws JieIOException {
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new byte[0];
        }
        try {
            int length = Math.min(number, source.remaining());
            if (length <= 0) {
                return null;
            }
            byte[] result = new byte[length];
            source.get(result);
            return result;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Marks and reads all bytes from source buffer into an array, resets the buffer after reading.
     * Returns the array, or null if no data read out and reaches to the end of buffer.
     *
     * @param source source buffer
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    @Nullable
    public static byte[] readReset(ByteBuffer source) throws JieIOException {
        try {
            source.mark();
            byte[] result = read(source);
            source.reset();
            return result;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Marks and reads specified number of bytes from source buffer into an array, resets the buffer after reading.
     * Returns the array, or null if no data read out and reaches to the end of buffer.
     * <p>
     * If the number &lt; 0, read all as {@link #read(ByteBuffer)};
     * els if the number is 0, no read and return an empty array;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the buffer.
     *
     * @param source source buffer
     * @param number specified number
     * @return the array, or null if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    @Nullable
    public static byte[] readReset(ByteBuffer source, int number) throws JieIOException {
        try {
            source.mark();
            byte[] result = read(source, number);
            source.reset();
            return result;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads bytes from source buffer into dest buffer,
     * returns actual read number, or -1 if no data read out and reaches to the end of buffer.
     * This method will keep reading until the dest buffer is filled up,
     * or the reading reaches the end of the buffer.
     *
     * @param source source buffer
     * @param dest   dest buffer
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    public static int readTo(ByteBuffer source, ByteBuffer dest) throws JieIOException {
        try {
            int sr = source.remaining();
            int dr = dest.remaining();
            if (sr <= dr) {
                dest.put(source);
                return sr;
            }
            ByteBuffer slice = readSlice(source, dr);
            dest.put(slice);
            return dr;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads specified number of bytes from source buffer into dest buffer,
     * returns actual read number, or -1 if no data read out and reaches to the end of buffer.
     * <p>
     * If the number &lt; 0, read as {@link #readTo(ByteBuffer, ByteBuffer)};
     * els if the number is 0, no read and return 0;
     * else this method will keep reading until the read number reaches to the specified number,
     * or the reading reaches the end of the buffer (of source or dest).
     *
     * @param source source buffer
     * @param dest   dest buffer
     * @param number specified number
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    public static int readTo(ByteBuffer source, ByteBuffer dest, int number) throws JieIOException {
        if (number < 0) {
            return readTo(source, dest);
        }
        if (number == 0) {
            return 0;
        }
        try {
            ByteBuffer src = source.remaining() == number ? source : slice(source, number);
            int readNum = readTo(src, dest);
            if (readNum > 0) {
                source.position(source.position() + readNum);
            }
            return readNum;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads specified number of bytes from source buffer into dest array,
     * returns actual read number, or -1 if no data read out and reaches to the end of buffer or array.
     *
     * @param source source buffer
     * @param dest   dest array
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer or array
     * @throws JieIOException IO exception
     */
    public static int readTo(ByteBuffer source, byte[] dest) throws JieIOException {
        return readTo(source, dest, 0);
    }

    /**
     * Reads specified number of bytes from source buffer into dest array starting from given offset,
     * returns actual read number, or -1 if no data read out and reaches to the end of buffer or array.
     *
     * @param source source buffer
     * @param dest   dest array
     * @param offset given offset
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer or array
     * @throws JieIOException IO exception
     */
    public static int readTo(ByteBuffer source, byte[] dest, int offset) throws JieIOException {
        try {
            JieCheck.checkInBounds(offset, 0, dest.length);
            int minLen = Math.min(source.remaining(), dest.length - offset);
            if (minLen <= 0) {
                return 0;
            }
            if (source.remaining() >= minLen) {
                source.get(dest, offset, minLen);
            } else {
                ByteBuffer slice = readSlice(source, minLen);
                slice.get(dest, offset, minLen);
            }
            return minLen;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads bytes from source buffer into dest stream,
     * returns actual read number, or -1 if no data read out and reaches to the end of buffer.
     * This method will keep reading until the reading reaches the end of the buffer.
     *
     * @param source source buffer
     * @param dest   dest stream
     * @return actual read number, or -1 if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    public static int readTo(ByteBuffer source, OutputStream dest) throws JieIOException {
        try {
            if (source.hasArray()) {
                int remaining = source.remaining();
                dest.write(source.array(), source.arrayOffset() + source.position(), remaining);
                source.position(source.position() + remaining);
                return remaining;
            }
            byte[] bytes = read(source);
            dest.write(bytes);
            return bytes.length;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Reads all bytes from source buffer into a string with {@link JieChars#defaultCharset()}.
     * Returns the string, or null if no data read out and reaches to the end of buffer.
     *
     * @param source source buffer
     * @return the string, or null if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String readString(ByteBuffer source) throws JieIOException {
        return readString(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from source buffer into a string with specified charset.
     * Returns the string, or null if no data read out and reaches to the end of buffer.
     *
     * @param source  source buffer
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of buffer
     * @throws JieIOException IO exception
     */
    @Nullable
    public static String readString(ByteBuffer source, Charset charset) throws JieIOException {
        try {
            byte[] bytes = read(source);
            if (bytes == null) {
                return null;
            }
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Returns slice of given buffer by {@link ByteBuffer#slice()}, and sets the slice's limit to specified number
     * (or remaining if remaining is less than specified number).
     * Position of given buffer will not be changed.
     *
     * @param buffer given buffer
     * @param number specified number
     * @return the slice buffer
     * @throws JieIOException IO exception
     */
    public static ByteBuffer slice(ByteBuffer buffer, int number) throws JieIOException {
        try {
            ByteBuffer slice = buffer.slice();
            slice.limit(Math.min(number, buffer.remaining()));
            return slice;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Returns slice of given buffer by {@link ByteBuffer#slice()}, and sets the slice's limit to specified number
     * (or remaining if remaining is less than specified number).
     * Position of given buffer will be set to {@code (buffer.position + slice.remaining())}.
     *
     * @param buffer given buffer
     * @param number specified number
     * @return the slice buffer
     * @throws JieIOException IO exception
     */
    public static ByteBuffer readSlice(ByteBuffer buffer, int number) throws JieIOException {
        try {
            ByteBuffer slice = buffer.slice();
            slice.limit(Math.min(number, buffer.remaining()));
            buffer.position(buffer.position() + slice.remaining());
            return slice;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Returns a sub-range view of given buffer, starting from given offset to buffer's limit.
     * The two buffers will share the same data so any operation will reflect each other.
     * <p>
     * Note the offset is counted from 0, not {@code buffer.position()}.
     *
     * @param buffer given buffer
     * @param offset given offset
     * @return the sub-buffer
     * @throws JieIOException IO exception
     */
    public static ByteBuffer subBuffer(ByteBuffer buffer, int offset) throws JieIOException {
        try {
            JieCheck.checkInBounds(offset, 0, buffer.limit());
            int pos = buffer.position();
            buffer.position(offset);
            ByteBuffer slice = buffer.slice();
            buffer.position(pos);
            return slice;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Returns a sub-range view of given buffer, starting from given offset to specified length.
     * The two buffers will share the same data so any operation will reflect each other.
     * <p>
     * Note the offset is counted from 0, not {@code buffer.position()}.
     *
     * @param buffer given buffer
     * @param offset given offset
     * @param length specified length
     * @return the sub-buffer
     * @throws JieIOException IO exception
     */
    public static ByteBuffer subBuffer(ByteBuffer buffer, int offset, int length) throws JieIOException {
        try {
            JieCheck.checkRangeInBounds(offset, offset + length, 0, buffer.limit());
            int pos = buffer.position();
            buffer.position(offset);
            ByteBuffer slice = slice(buffer, length);
            buffer.position(pos);
            return slice;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Splits given buffer in specified length, returns split buffer list.
     * This method starts the loop:
     * <ul>
     *     <li>
     *         If remaining length is &gt;= specified length, this method will use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified length. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than specified length, loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer given buffer
     * @param length specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, int length) {
        return split(buffer, length, len -> null);
    }

    /**
     * Splits given buffer in specified length, returns split buffer list.
     * This method starts the loop:
     * <ul>
     *     <li>
     *         If remaining length is &gt;= specified length, this method will call {@code generator} with specified
     *         length to generate new buffer. If {@code generator} return a new buffer, this method will fill data from
     *         given buffer into new buffer. If {@code generator} return null, use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified buffer. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than specified length, loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer    given buffer
     * @param length    specified length
     * @param generator given buffer generator, the argument is specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, int length, IntFunction<ByteBuffer> generator) {
        int remaining = buffer.remaining();
        if (remaining <= 0) {
            return Collections.emptyList();
        }
        if (remaining < length) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = new ArrayList<>(remaining / length);
        while (buffer.remaining() >= length) {
            ByteBuffer newBuffer = generator.apply(length);
            if (newBuffer == null) {
                result.add(readSlice(buffer, length));
            } else {
                readTo(buffer, newBuffer);
                newBuffer.flip();
                result.add(newBuffer);
            }
        }
        return result;
    }

    /**
     * Splits given buffer in specified length, returns split buffer list.
     * This method assumes the length is specified at specified length offset, and starts the loop:
     * <ul>
     *     <li>
     *         Marks current position.
     *         If remaining length is &gt;= {@code lengthOffset + lengthSize}, this method will skip and read number of
     *         {@code lengthSize} bytes at {@code lengthOffset} as specified length.
     *     </li>
     *     <li>
     *         Resets current position.
     *         If remaining length is &gt;= specified length, this method will use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified length. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than {@code lengthOffset + lengthSize} or specified length,
     *         loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   byte number of length, must in 1, 2, 4
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, int lengthOffset, int lengthSize) {
        return split(buffer, lengthOffset, lengthSize, len -> null);
    }

    /**
     * Splits given buffer in specified length, returns split buffer list.
     * This method assumes the length is specified at specified length offset, and starts the loop:
     * <ul>
     *     <li>
     *         Marks current position.
     *         If remaining length is &gt;= {@code lengthOffset + lengthSize}, this method will skip and read number of
     *         {@code lengthSize} bytes at {@code lengthOffset} as specified length.
     *     </li>
     *     <li>
     *         Resets current position.
     *         If remaining length is &gt;= specified length, this method will call {@code generator} with specified
     *         length to generate new buffer. If {@code generator} return a new buffer, this method will fill data from
     *         given buffer into new buffer. If {@code generator} return null, use {@link ByteBuffer#slice()} to
     *         generate a slice buffer of specified buffer. Then moves the position with specified length.
     *     </li>
     *     <li>
     *         If remaining length is less than {@code lengthOffset + lengthSize} or specified length,
     *         loop will be broken.
     *     </li>
     * </ul>
     *
     * @param buffer       given buffer
     * @param lengthOffset offset of length
     * @param lengthSize   byte number of length, must in 1, 2, 4
     * @param generator    given buffer generator, the argument is specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(
        ByteBuffer buffer, int lengthOffset, int lengthSize, IntFunction<ByteBuffer> generator) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        int minSize = lengthOffset + lengthSize;
        if (buffer.remaining() < minSize) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = new LinkedList<>();
        while (true) {
            buffer.mark();
            buffer.position(buffer.position() + lengthOffset);
            int length = readLength(buffer, lengthSize);
            buffer.reset();
            if (buffer.remaining() < length) {
                break;
            }
            ByteBuffer newBuffer = generator.apply(length);
            if (newBuffer == null) {
                result.add(readSlice(buffer, length));
            } else {
                readTo(buffer, newBuffer);
                newBuffer.flip();
                result.add(newBuffer);
            }
            if (buffer.remaining() < minSize) {
                break;
            }
        }
        return result;
    }

    private static int readLength(ByteBuffer buffer, int lengthSize) {
        switch (lengthSize) {
            case 1:
                return buffer.get() & 0x000000ff;
            case 2:
                return buffer.getShort() & 0x0000ffff;
            case 4:
                return buffer.getInt();
        }
        throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
    }

    /**
     * Splits given buffer in specified delimiter, returns split buffer list.
     * This method starts the loop:
     * <ul>
     *     <li>
     *         Marks current position, reads until meets specified delimiter.
     *     </li>
     *     <li>
     *         If specified delimiter is met, this method will use {@link ByteBuffer#slice()} to generate a slice
     *         buffer of read data (delimiter exclusive). Then moves the position to next of delimiter.
     *     </li>
     *     <li>
     *         If no specified delimiter is met, reset position and loop will be broken.
     *     </li>
     * </ul>
     * For example:
     * <pre>
     *     split("123|456|789|") = ["123", "456", "789"]
     *     split("|123|456|789|") = ["", "123", "456", "789"]
     *     split("|123|456|78") = ["", "123", "456"] and reset to position 9
     * </pre>
     *
     * @param buffer    given buffer
     * @param delimiter specified delimiter
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, byte delimiter) {
        return split(buffer, delimiter, len -> null);
    }

    /**
     * Splits given buffer in specified delimiter, returns split buffer list.
     * This method starts the loop:
     * <ul>
     *     <li>
     *         Marks current position, reads until meets specified delimiter.
     *     </li>
     *     <li>
     *         If specified delimiter is met, this method will call {@code generator} with length of read data to
     *         generate new buffer. If {@code generator} return a new buffer, this method will fill data from given
     *         buffer into new buffer. If {@code generator} return null, use {@link ByteBuffer#slice()} to generate a
     *         slice buffer of read data (delimiter exclusive).
     *         Then moves the position to next of delimiter.
     *     </li>
     *     <li>
     *         If no specified delimiter is met, reset position and loop will be broken.
     *     </li>
     * </ul>
     * For example:
     * <pre>
     *     split("123|456|789|") = ["123", "456", "789"]
     *     split("|123|456|789|") = ["", "123", "456", "789"]
     *     split("|123|456|78") = ["", "123", "456"] and reset to position 9
     * </pre>
     *
     * @param buffer    given buffer
     * @param delimiter specified delimiter
     * @param generator given buffer generator, the argument is specified length
     * @return split buffer list
     */
    public static List<ByteBuffer> split(ByteBuffer buffer, byte delimiter, IntFunction<ByteBuffer> generator) {
        if (!buffer.hasRemaining()) {
            return Collections.emptyList();
        }
        List<ByteBuffer> result = null;
        buffer.mark();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            int pos = buffer.position();
            if (b == delimiter) {
                int delimiterPos = pos - 1;
                buffer.reset();
                int length = delimiterPos - buffer.position();
                ByteBuffer newBuffer = generator.apply(length);
                if (result == null) {
                    result = new LinkedList<>();
                }
                if (newBuffer == null) {
                    result.add(readSlice(buffer, length));
                } else {
                    readTo(buffer, newBuffer);
                    newBuffer.flip();
                    result.add(newBuffer);
                }
                buffer.position(pos);
                buffer.mark();
            }
        }
        buffer.reset();
        return result == null ? Collections.emptyList() : result;
    }

    /**
     * Returns whether given buffer is a simple wrapper of back array:
     * <pre>
     *     return buffer.hasArray()
     *             && buffer.position() == 0
     *             && buffer.arrayOffset() == 0
     *             && buffer.limit() == buffer.array().length;
     * </pre>
     *
     * @param buffer given buffer
     * @return whether given buffer is a simple wrapper of back array
     */
    public static boolean isSimpleWrapper(ByteBuffer buffer) {
        return buffer.hasArray()
            && buffer.position() == 0
            && buffer.arrayOffset() == 0
            && buffer.limit() == buffer.array().length;
    }

    /**
     * Returns back array if {@link #isSimpleWrapper(ByteBuffer)} returns true for given buffer,
     * and the position will be set to {@code buffer.limit()}.
     * Otherwise, return {@link #read(ByteBuffer)}.
     *
     * @param buffer given buffer
     * @return back array if {@link #isSimpleWrapper(ByteBuffer)} returns true for given buffer
     */
    public static byte[] readBack(ByteBuffer buffer) {
        if (isSimpleWrapper(buffer)) {
            buffer.position(buffer.limit());
            return buffer.array();
        }
        return read(buffer);
    }

    // File methods:

    /**
     * Wraps given random access file as an input stream, supports mark/reset.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @return wrapped stream
     * @throws JieIOException IO exception
     */
    public static InputStream toInputStream(RandomAccessFile random) throws JieIOException {
        return toInputStream(random, 0, -1);
    }

    /**
     * Wraps given random access file as an input stream,
     * readable bytes from {@code offset} position to {@code (offset + length)},
     * supports mark/reset.
     * {@code length} can be set to -1 if to read to end of the file.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @param offset offset position to start read
     * @param length length of readable bytes, or -1 to read to end of file
     * @return wrapped stream
     * @throws JieIOException IO exception
     */
    public static InputStream toInputStream(RandomAccessFile random, long offset, long length) throws JieIOException {
        return new RandomInputStream(random, offset, length);
    }

    /**
     * Wraps given random access file as an output stream.
     * The stream will lock the file with exclusive lock by {@link FileChannel#tryLock(long, long, boolean)}.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @return wrapped stream
     * @throws JieIOException IO exception
     */
    public static OutputStream toOutputStream(RandomAccessFile random) throws JieIOException {
        return toOutputStream(random, 0, -1);
    }

    /**
     * Wraps given random access file as an output stream,
     * written bytes from {@code offset} position to {@code (offset + length)}.
     * {@code length} can be set to -1 if to write unlimitedly.
     * The stream will lock the file with exclusive lock by {@link FileChannel#tryLock(long, long, boolean)}.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @param offset offset position to start write
     * @param length length of written bytes, or -1 to write unlimitedly
     * @return wrapped stream
     * @throws JieIOException IO exception
     */
    public static OutputStream toOutputStream(RandomAccessFile random, long offset, long length) throws JieIOException {
        return new RandomOutputStream(random, offset, length);
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given file of path.
     *
     * @param path given file of path
     * @return read bytes
     */
    public static byte[] readBytes(Path path) {
        return readBytes(path, 0, -1);
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given file of path from offset position,
     * the given length may be set to -1 to read to end of file.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to read to end of file
     * @return read bytes
     */
    public static byte[] readBytes(Path path, long offset, long length) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "r")) {
            return JieIO.read(JieIO.toInputStream(random, offset, length));
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given file of path.
     * The read bytes will be encoded to String with {@link JieChars#defaultCharset()}.
     *
     * @param path given file of path
     * @return read string
     */
    public static String readString(Path path) {
        return readString(path, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given file of path.
     * The read bytes will be encoded to String with given charset.
     *
     * @param path    given file of path
     * @param charset given charset
     * @return read string
     */
    public static String readString(Path path, Charset charset) {
        return readString(path, 0, -1, charset);
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given file of path from offset position,
     * the given length may be set to -1 to read to end of file.
     * The read bytes will be encoded to String with {@link JieChars#defaultCharset()}.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to read to end of file
     * @return read string
     */
    public static String readString(Path path, long offset, long length) {
        return readString(path, offset, length, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given file of path from offset position,
     * the given length may be set to -1 to read to end of file.
     * The read bytes will be encoded to String with given charset.
     *
     * @param path    given file of path
     * @param offset  offset position
     * @param length  given length, maybe -1 to read to end of file
     * @param charset given charset
     * @return read string
     */
    public static String readString(Path path, long offset, long length, Charset charset) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "r")) {
            return JieIO.readString(JieIO.toInputStream(random, offset, length), charset);
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to write  bytes into given file of path.
     *
     * @param path given file of path
     * @param data given data
     */
    public static void writeBytes(Path path, InputStream data) {
        writeBytes(path, 0, -1, data);
    }

    /**
     * Using {@link RandomAccessFile} to write given length bytes into given file of path from offset position,
     * the given length may be set to -1 to write unlimitedly.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to write unlimitedly
     * @param data   given data
     */
    public static void writeBytes(Path path, long offset, long length, InputStream data) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "rw")) {
            OutputStream dest = JieIO.toOutputStream(random, offset, length);
            JieIO.readTo(data, dest);
            dest.flush();
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given file.
     * The written bytes will be decoded from given data with {@link JieChars#defaultCharset()}.
     *
     * @param path given file of path
     * @param data given data
     */
    public static void writeString(Path path, CharSequence data) {
        writeString(path, data, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given file.
     * The written bytes will be decoded from given data with given charset.
     *
     * @param path    given file of path
     * @param data    given data
     * @param charset given charset
     */
    public static void writeString(Path path, CharSequence data, Charset charset) {
        writeString(path, 0, -1, data, charset);
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given file of path from offset position,
     * the given length may be set to -1 to write unlimitedly.
     * The written bytes will be decoded from given data with {@link JieChars#defaultCharset()}.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to write unlimitedly
     * @param data   given data
     */
    public static void writeString(Path path, long offset, long length, CharSequence data) {
        writeString(path, offset, length, data, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given file of path from offset position,
     * the given length may be set to -1 to write unlimitedly.
     * The written bytes will be decoded from given data with given charset.
     *
     * @param path    given file of path
     * @param offset  offset position
     * @param length  given length, maybe -1 to write unlimitedly
     * @param data    given data
     * @param charset given charset
     */
    public static void writeString(Path path, long offset, long length, CharSequence data, Charset charset) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "rw")) {
            Writer writer = JieIO.toWriter(JieIO.toOutputStream(random, offset, length), charset);
            writer.append(data);
            writer.flush();
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }
}
