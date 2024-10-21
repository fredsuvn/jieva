package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieChars;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Function;

/**
 * IO (Input/Output) utilities.
 *
 * @author fresduvn
 */
public class JieIO {

    /**
     * Default IO buffer size: 1024 * 8 = 8192.
     */
    public static final int BUFFER_SIZE = 1024 * 8;

    /**
     * Reads all bytes from source stream into an array. Returns the array, or null if no data read out and reaches to
     * the end of stream.
     *
     * @param source source stream
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static byte[] read(InputStream source) throws IORuntimeException {
        try {
            int available = source.available();
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
                        ByteArrayOutputStream dest = new ByteArrayOutputStream(available + 1);
                        dest.write(bytes);
                        dest.write(r);
                        transfer(source, dest);
                        return dest.toByteArray();
                    }
                } else {
                    return Arrays.copyOf(bytes, c);
                }
            } else {
                ByteArrayOutputStream dest = new ByteArrayOutputStream();
                long num = transfer(source, dest);
                return num < 0 ? null : dest.toByteArray();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads specified number of bytes from source stream into an array. Returns the array, or null if no data read out
     * and reaches to the end of stream.
     * <p>
     * If the number &lt; 0, read all as {@link #read(InputStream)}; els if the number is 0, no read and return an empty
     * array; else this method will keep reading until the read number reaches to the specified number, or the reading
     * reaches the end of the stream.
     *
     * @param source source stream
     * @param number specified number
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static byte[] read(InputStream source, int number) throws IORuntimeException {
        try {
            if (number < 0) {
                return read(source);
            }
            if (number == 0) {
                return new byte[0];
            }
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
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all chars from source reader into a string. Returns the string, or null if no data read out and reaches to
     * the end of reader.
     *
     * @param source source reader
     * @return the string, or null if no data read out and reaches to the end of reader
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String read(Reader source) throws IORuntimeException {
        StringBuilder dest = new StringBuilder();
        long readCount = transfer(source, dest);
        if (readCount == -1) {
            return null;
        }
        return dest.toString();
    }

    /**
     * Reads specified number of chars from source reader into a string. Returns the string, or null if no data read out
     * and reaches to the end of reader.
     * <p>
     * If the number &lt; 0, read all as {@link #read(Reader)}; els if the number is 0, no read and return an empty
     * array; else this method will keep reading until the read number reaches to the specified number, or the reading
     * reaches the end of the reader.
     *
     * @param source source reader
     * @param number specified number
     * @return the string, or null if no data read out and reaches to the end of reader
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String read(Reader source, int number) throws IORuntimeException {
        StringBuilder dest = new StringBuilder();
        long readCount = transfer(source, dest, number);
        if (readCount == -1) {
            return null;
        }
        return dest.toString();
    }

    /**
     * Reads all bytes from source stream into a string with {@link JieChars#defaultCharset()}. Returns the string, or
     * null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String readString(InputStream source) throws IORuntimeException {
        return readString(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from source stream into a string with specified charset. Returns the string, or null if no data
     * read out and reaches to the end of stream.
     *
     * @param source  source stream
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String readString(InputStream source, Charset charset) throws IORuntimeException {
        byte[] bytes = read(source);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    /**
     * Reads available bytes from source stream into an array. Returns the array, or null if no data read out and
     * reaches to the end of stream.
     *
     * @param source source stream
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static byte[] available(InputStream source) throws IORuntimeException {
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
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads available bytes from source stream into a string with {@link JieChars#defaultCharset()}. Returns the
     * string, or null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String avalaibleString(InputStream source) throws IORuntimeException {
        return avalaibleString(source, JieChars.defaultCharset());
    }

    /**
     * Reads available bytes from source stream into a string with specified charset. Returns the string, or null if no
     * data read out and reaches to the end of stream.
     *
     * @param source  source stream
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String avalaibleString(InputStream source, Charset charset) throws IORuntimeException {
        try {
            byte[] bytes = available(source);
            if (bytes == null) {
                return null;
            }
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Transfers bytes from source stream into dest array, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link BytesTransfer}):
     * <pre>
     *     return (int) BytesTransfer.from(source).to(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see BytesTransfer
     */
    public static int transfer(InputStream source, byte[] dest) throws IORuntimeException {
        return (int) BytesTransfer.from(source).to(dest).readLimit(dest.length).start();
    }

    /**
     * Transfers bytes from source stream into dest array within specified offset and length, returns actual read
     * number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link BytesTransfer}):
     * <pre>
     *     return (int) BytesTransfer.from(source).to(dest, offset, length).readLimit(length).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest array
     * @param offset specified offset at which the data write start
     * @param length specified length to read and write
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see BytesTransfer
     */
    public static int transfer(InputStream source, byte[] dest, int offset, int length) throws IORuntimeException {
        return (int) BytesTransfer.from(source).to(dest, offset, length).readLimit(length).start();
    }

    /**
     * Transfers bytes from source stream into dest buffer, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link BytesTransfer}):
     * <pre>
     *     return (int) BytesTransfer.from(source).to(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see BytesTransfer
     */
    public static int transfer(InputStream source, ByteBuffer dest) throws IORuntimeException {
        return (int) BytesTransfer.from(source).to(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Transfers bytes from source stream into dest stream, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link BytesTransfer}):
     * <pre>
     *     return (int) BytesTransfer.from(source).to(dest).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest stream
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see BytesTransfer
     */
    public static long transfer(InputStream source, OutputStream dest) throws IORuntimeException {
        return (int) BytesTransfer.from(source).to(dest).start();
    }

    /**
     * Transfers bytes from source stream into dest stream within specified read limit, returns actual read number. If
     * the source has been ended and no data read out, return -1. This method is equivalent to ({@link BytesTransfer}):
     * <pre>
     *     return (int) BytesTransfer.from(source).to(dest).readLimit(readLimit).start();
     * </pre>
     *
     * @param source    source stream
     * @param dest      dest stream
     * @param readLimit specified read limit
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see BytesTransfer
     */
    public static long transfer(InputStream source, OutputStream dest, long readLimit) throws IORuntimeException {
        return (int) BytesTransfer.from(source).to(dest).readLimit(readLimit).start();
    }

    /**
     * Transfers bytes from source stream into dest stream within specified read limit and block size, returns actual
     * read number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link BytesTransfer}):
     * <pre>
     *     return (int) BytesTransfer.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
     * </pre>
     *
     * @param source    source stream
     * @param dest      dest stream
     * @param readLimit specified read limit
     * @param blockSize specified block size
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see BytesTransfer
     */
    public static long transfer(InputStream source, OutputStream dest, long readLimit, int blockSize) throws IORuntimeException {
        return (int) BytesTransfer.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
    }

    /**
     * Transfers chars from source reader into dest array, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link CharsTransfer}):
     * <pre>
     *     return (int) CharsTransfer.from(source).to(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharsTransfer
     */
    public static int transfer(Reader source, char[] dest) throws IORuntimeException {
        return (int) CharsTransfer.from(source).to(dest).readLimit(dest.length).start();
    }

    /**
     * Transfers chars from source reader into dest array within specified offset and length, returns actual read
     * number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link CharsTransfer}):
     * <pre>
     *     return (int) CharsTransfer.from(source).to(dest, offset, length).readLimit(length).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest array
     * @param offset specified offset at which the data write start
     * @param length specified length to read and write
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharsTransfer
     */
    public static int transfer(Reader source, char[] dest, int offset, int length) throws IORuntimeException {
        return (int) CharsTransfer.from(source).to(dest, offset, length).readLimit(length).start();
    }

    /**
     * Transfers chars from source reader into dest buffer, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link CharsTransfer}):
     * <pre>
     *     return (int) CharsTransfer.from(source).to(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharsTransfer
     */
    public static int transfer(Reader source, CharBuffer dest) throws IORuntimeException {
        return (int) CharsTransfer.from(source).to(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable, returns actual read number. If the source has been ended
     * and no data read out, return -1. This method is equivalent to ({@link CharsTransfer}):
     * <pre>
     *     return (int) CharsTransfer.from(source).to(dest).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest appendable
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharsTransfer
     */
    public static long transfer(Reader source, Appendable dest) throws IORuntimeException {
        return (int) CharsTransfer.from(source).to(dest).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable within specified read limit, returns actual read number.
     * If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link CharsTransfer}):
     * <pre>
     *     return (int) CharsTransfer.from(source).to(dest).readLimit(readLimit).start();
     * </pre>
     *
     * @param source    source reader
     * @param dest      dest appendable
     * @param readLimit specified read limit
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharsTransfer
     */
    public static long transfer(Reader source, Appendable dest, int readLimit) throws IORuntimeException {
        return (int) CharsTransfer.from(source).to(dest).readLimit(readLimit).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable within specified read limit and block size, returns
     * actual read number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link CharsTransfer}):
     * <pre>
     *     return (int) CharsTransfer.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
     * </pre>
     *
     * @param source    source reader
     * @param dest      dest appendable
     * @param readLimit specified read limit
     * @param blockSize specified block size
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharsTransfer
     */
    public static long transfer(Reader source, Appendable dest, int readLimit, int blockSize) throws IORuntimeException {
        return (int) CharsTransfer.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
    }

    /**
     * Wraps given array as an {@link InputStream}.
     * <p>
     * The returned stream is similar to {@link ByteArrayInputStream} but is not the same, its methods are not modified
     * by {@code synchronized} and thus do not guarantee thread safety. It also supports mark/reset operations, and the
     * close method does nothing (similar to {@link ByteArrayInputStream}).
     *
     * @param array given array
     * @return given array as an {@link InputStream}
     */
    public static InputStream wrapIn(byte[] array) {
        return new BytesInputStream(array);
    }

    /**
     * Wraps given array as an {@link InputStream} from specified offset up to specified length.
     * <p>
     * The returned stream is similar to {@link ByteArrayInputStream} but is not the same, its methods are not modified
     * by {@code synchronized} and thus do not guarantee thread safety. It also supports mark/reset operations, and the
     * close method does nothing (similar to {@link ByteArrayInputStream}).
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link InputStream}
     */
    public static InputStream wrapIn(byte[] array, int offset, int length) {
        return new BytesInputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link InputStream}.
     * <p>
     * Returned stream does not guarantee thread safety. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link InputStream}
     */
    public static InputStream wrapIn(ByteBuffer buffer) {
        return new BufferInputStream(buffer);
    }

    /**
     * Wraps given random access file as an {@link InputStream} from specified initial file pointer.
     * <p>
     * Returned stream does not guarantee thread safety. It supports mark/reset operations, and first seeks to specified
     * initial file pointer when creating the stream and re-seeks if calls reset method. The close method will close the
     * file.
     * <p>
     * Note that if anything else seeks this file, it will affect this stream.
     *
     * @param random      given random access file
     * @param initialSeek specified initial file pointer
     * @return given random access file as an {@link InputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream wrapIn(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        try {
            return new RandomInputStream(random, initialSeek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Wraps given reader as an {@link InputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Returned stream does not guarantee thread safety. It does support mark/reset operations. The read position of the
     * reader and stream may not correspond, the close method will close both reader and stream at their current
     * positions.
     *
     * @param reader given reader
     * @return given reader as an {@link InputStream}
     */
    public static InputStream wrapIn(Reader reader) {
        return wrapIn(reader, JieChars.defaultCharset());
    }

    /**
     * Wraps given reader as an {@link InputStream} with specified charset.
     * <p>
     * Returned stream does not guarantee thread safety. It does support mark/reset operations. The read position of the
     * reader and stream may not correspond, the close method will close both reader and stream at their current
     * positions.
     *
     * @param reader  given reader
     * @param charset specified charset
     * @return given reader as an {@link InputStream}
     */
    public static InputStream wrapIn(Reader reader, Charset charset) {
        return new CharsInputStream(reader, charset);
    }

    /**
     * Wraps given array as an {@link OutputStream}.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the array. Close method
     * does nothing.
     *
     * @param array given array
     * @return given array as an {@link OutputStream}
     */
    public static OutputStream wrapOut(byte[] array) {
        return new BytesOutputStream(array);
    }

    /**
     * Wraps given array as {@link OutputStream} from specified offset up to specified length.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the array. Close method
     * does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link OutputStream}
     */
    public static OutputStream wrapOut(byte[] array, int offset, int length) {
        return new BytesOutputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link OutputStream}.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link OutputStream}
     */
    public static OutputStream wrapOut(ByteBuffer buffer) {
        return new BufferOutputStream(buffer);
    }

    /**
     * Wraps given random access file as an {@link OutputStream} from specified initial file pointer.
     * <p>
     * Returned stream does not guarantee thread safety. It first seeks to specified initial file pointer when creating
     * the stream. The close method will close the file.
     * <p>
     * Note that if anything else seeks this file, it will affect this stream.
     *
     * @param random      given random access file
     * @param initialSeek specified initial file pointer
     * @return given random access file as an {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream wrapOut(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        try {
            return new RandomOutputStream(random, initialSeek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Wraps given char appender as an {@link OutputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Returned stream does not guarantee thread safety. The written position of the appender and stream may not
     * correspond, the close method will close both appender and stream at their current positions.
     *
     * @param appender given char appender
     * @return given char appender as an {@link OutputStream}
     */
    public static OutputStream wrapOut(Appendable appender) {
        return wrapOut(appender, JieChars.defaultCharset());
    }

    /**
     * Wraps given char appender as an {@link OutputStream} with specified charset.
     * <p>
     * Returned stream does not guarantee thread safety. The written position of the appender and stream may not
     * correspond, the close method will close both appender and stream at their current positions.
     *
     * @param appender given char appender
     * @param charset  specified charset
     * @return given char appender as an {@link OutputStream}
     */
    public static OutputStream wrapOut(Appendable appender, Charset charset) {
        return new CharsOutputStream(appender, charset);
    }

    /**
     * Wraps given array as an {@link Reader}.
     * <p>
     * The returned stream is similar to {@link CharArrayReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param array given array
     * @return given array as an {@link Reader}
     */
    public static Reader wrapReader(char[] array) {
        return new BufferReader(array);
    }

    /**
     * Wraps given array as an {@link Reader} from specified offset up to specified length.
     * <p>
     * The returned stream is similar to {@link CharArrayReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link Reader}
     */
    public static Reader wrapReader(char[] array, int offset, int length) {
        return new BufferReader(array, offset, length);
    }

    /**
     * Wraps given chars as an {@link Reader}.
     * <p>
     * The returned stream is similar to {@link StringReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param chars given chars
     * @return given array as an {@link Reader}
     */
    public static Reader wrapReader(CharSequence chars) {
        return new BufferReader(chars);
    }

    /**
     * Wraps given buffer as an {@link Reader}.
     * <p>
     * Returned reader does not guarantee thread safety. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link Reader}
     */
    public static Reader wrapReader(CharBuffer buffer) {
        return new BufferReader(buffer);
    }

    /**
     * Wraps given array as an {@link Writer}.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param array given array
     * @return given array as an {@link Writer}
     */
    public static Writer wrapWriter(char[] array) {
        return new BufferWriter(array);
    }

    /**
     * Wraps given array as {@link Writer} from specified offset up to specified length.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link Writer}
     */
    public static Writer wrapWriter(char[] array, int offset, int length) {
        return new BufferWriter(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link Writer}.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link Writer}
     */
    public static Writer wrapWriter(CharBuffer buffer) {
        return new BufferWriter(buffer);
    }

    /**
     * Wraps given stream as {@link Reader} with {@link JieChars#defaultCharset()}.
     *
     * @param stream given stream
     * @return given stream as {@link Reader}
     * @throws IORuntimeException IO runtime exception
     */
    public static Reader toReader(InputStream stream) throws IORuntimeException {
        return toReader(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps given stream as {@link Reader} with specified charset.
     *
     * @param stream  given stream
     * @param charset specified charset
     * @return given stream as {@link Reader}
     * @throws IORuntimeException IO runtime exception
     */
    public static Reader toReader(InputStream stream, Charset charset) throws IORuntimeException {
        return new InputStreamReader(stream, charset);
    }

    /**
     * Wraps given stream as {@link Writer} with {@link JieChars#defaultCharset()}.
     *
     * @param stream given stream
     * @return given stream as {@link Writer}
     * @throws IORuntimeException IO runtime exception
     */
    public static Writer toWriter(OutputStream stream) throws IORuntimeException {
        return toWriter(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps given stream as {@link Writer} with {@link JieChars#defaultCharset()}.
     *
     * @param stream  given stream
     * @param charset specified charset
     * @return given stream as {@link Writer}
     * @throws IORuntimeException IO runtime exception
     */
    public static Writer toWriter(OutputStream stream, Charset charset) throws IORuntimeException {
        return new OutputStreamWriter(stream, charset);
    }

    /**
     * Returns a {@link TransformInputStream} with source stream, block size and transformer.
     * <p>
     * Note the block size could be negative or {@code 0}, in this case all bytes would be read once from source stream
     * to transform.
     * <p>
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

    /**
     * Returns an empty input stream.
     *
     * @return an empty input stream
     */
    public static InputStream emptyInputStream() {
        return EmptyInputStream.SINGLETON;
    }

    private static final class EmptyInputStream extends InputStream {

        private static final EmptyInputStream SINGLETON = new EmptyInputStream();

        @Override
        public int read() {
            return -1;
        }
    }
}
