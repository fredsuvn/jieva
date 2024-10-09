package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.JieCheck;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
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
     * Returns an instance of {@link ByteTransfer} to transfer bytes from specified source into specified destination.
     *
     * @return an instance of {@link ByteTransfer} to transfer bytes from specified source into specified destination
     * @see ByteTransfer
     */
    public static ByteTransfer byteTransfer() {
        return new ByteTransferImpl();
    }

    /**
     * Returns an instance of {@link CharTransfer} to transfer chars from specified source into specified destination.
     *
     * @return an instance of {@link CharTransfer} to transfer chars from specified source into specified destination
     * @see CharTransfer
     */
    public static CharTransfer charTransfer() {
        return new CharTransferImpl();
    }

    /**
     * Transfers bytes from source stream into dest array, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link #byteTransfer()}):
     * <pre>
     *     return (int) byteTransfer().input(source).output(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static int transfer(InputStream source, byte[] dest) throws IORuntimeException {
        return (int) byteTransfer().input(source).output(dest).readLimit(dest.length).start();
    }

    /**
     * Transfers bytes from source stream into dest array within specified offset and length, returns actual read
     * number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link #byteTransfer()}):
     * <pre>
     *     return (int) byteTransfer().input(source).output(dest, offset, length).readLimit(length).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest array
     * @param offset specified offset at which the data write start
     * @param length specified length to read and write
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static int transfer(InputStream source, byte[] dest, int offset, int length) throws IORuntimeException {
        return (int) byteTransfer().input(source).output(dest, offset, length).readLimit(length).start();
    }

    /**
     * Transfers bytes from source stream into dest buffer, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link #byteTransfer()}):
     * <pre>
     *     return (int) byteTransfer().input(source).output(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static int transfer(InputStream source, ByteBuffer dest) throws IORuntimeException {
        return (int) byteTransfer().input(source).output(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Transfers bytes from source stream into dest stream, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link #byteTransfer()}):
     * <pre>
     *     return (int) byteTransfer().input(source).output(dest).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest stream
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static long transfer(InputStream source, OutputStream dest) throws IORuntimeException {
        return (int) byteTransfer().input(source).output(dest).start();
    }

    /**
     * Transfers bytes from source stream into dest stream within specified read limit, returns actual read number. If
     * the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link #byteTransfer()}):
     * <pre>
     *     return (int) byteTransfer().input(source).output(dest).readLimit(readLimit).start();
     * </pre>
     *
     * @param source    source stream
     * @param dest      dest stream
     * @param readLimit specified read limit
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static long transfer(InputStream source, OutputStream dest, long readLimit) throws IORuntimeException {
        return (int) byteTransfer().input(source).output(dest).readLimit(readLimit).start();
    }

    /**
     * Transfers bytes from source stream into dest stream within specified read limit and block size, returns actual
     * read number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link #byteTransfer()}):
     * <pre>
     *     return (int) byteTransfer().input(source).output(dest).readLimit(readLimit).blockSize(blockSize).start();
     * </pre>
     *
     * @param source    source stream
     * @param dest      dest stream
     * @param readLimit specified read limit
     * @param blockSize specified block size
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static long transfer(InputStream source, OutputStream dest, long readLimit, int blockSize) throws IORuntimeException {
        return (int) byteTransfer().input(source).output(dest).readLimit(readLimit).blockSize(blockSize).start();
    }

    /**
     * Transfers chars from source reader into dest array, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link #byteTransfer()}):
     * <pre>
     *     return (int) charTransfer().input(source).output(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static int transfer(Reader source, char[] dest) throws IORuntimeException {
        return (int) charTransfer().input(source).output(dest).readLimit(dest.length).start();
    }

    /**
     * Transfers chars from source reader into dest array within specified offset and length, returns actual read
     * number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link #byteTransfer()}):
     * <pre>
     *     return (int) charTransfer().input(source).output(dest, offset, length).readLimit(length).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest array
     * @param offset specified offset at which the data write start
     * @param length specified length to read and write
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static int transfer(Reader source, char[] dest, int offset, int length) throws IORuntimeException {
        return (int) charTransfer().input(source).output(dest, offset, length).readLimit(length).start();
    }

    /**
     * Transfers chars from source reader into dest buffer, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link #byteTransfer()}):
     * <pre>
     *     return (int) charTransfer().input(source).output(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static int transfer(Reader source, CharBuffer dest) throws IORuntimeException {
        return (int) charTransfer().input(source).output(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable, returns actual read number. If the source has been ended
     * and no data read out, return -1. This method is equivalent to ({@link #byteTransfer()}):
     * <pre>
     *     return (int) charTransfer().input(source).output(dest).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest appendable
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static long transfer(Reader source, Appendable dest) throws IORuntimeException {
        return (int) charTransfer().input(source).output(dest).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable within specified read limit, returns actual read number.
     * If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link #byteTransfer()}):
     * <pre>
     *     return (int) charTransfer().input(source).output(dest).readLimit(readLimit).start();
     * </pre>
     *
     * @param source    source reader
     * @param dest      dest appendable
     * @param readLimit specified read limit
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static long transfer(Reader source, Appendable dest, int readLimit) throws IORuntimeException {
        return (int) charTransfer().input(source).output(dest).readLimit(readLimit).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable within specified read limit and block size, returns
     * actual read number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link #byteTransfer()}):
     * <pre>
     *     return (int) charTransfer().input(source).output(dest).readLimit(readLimit).blockSize(blockSize).start();
     * </pre>
     *
     * @param source    source reader
     * @param dest      dest appendable
     * @param readLimit specified read limit
     * @param blockSize specified block size
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     */
    public static long transfer(Reader source, Appendable dest, int readLimit, int blockSize) throws IORuntimeException {
        return (int) charTransfer().input(source).output(dest).readLimit(readLimit).blockSize(blockSize).start();
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
     * Wraps given buffer as {@link Reader}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link Reader}
     * @throws IORuntimeException IO runtime exception
     */
    public static Reader toReader(CharBuffer buffer) throws IORuntimeException {
        return new CharBufferReader(buffer);
    }

    /**
     * Wraps given reader as {@link InputStream} with {@link JieChars#defaultCharset()}. The returned stream doesn't
     * support mark/reset methods.
     *
     * @param reader given reader
     * @return given reader as {@link InputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream toInputStream(Reader reader) throws IORuntimeException {
        return toInputStream(reader, JieChars.defaultCharset());
    }

    /**
     * Wraps given reader as {@link InputStream} with specified charset. The returned stream doesn't support mark/reset
     * methods.
     *
     * @param reader  given reader
     * @param charset specified charset
     * @return given reader as {@link InputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream toInputStream(Reader reader, Charset charset) throws IORuntimeException {
        return new ReaderInputStream(reader, charset);
    }

    /**
     * Wraps given array as {@link ByteArrayInputStream}.
     *
     * @param array given array
     * @return given array as {@link ByteArrayInputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static ByteArrayInputStream toInputStream(byte[] array) throws IORuntimeException {
        return new ByteArrayInputStream(array);
    }

    /**
     * Wraps given array as {@link ByteArrayInputStream}, starting from given offset with specified length.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return given array as {@link ByteArrayInputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static ByteArrayInputStream toInputStream(byte[] array, int offset, int length) throws IORuntimeException {
        return new ByteArrayInputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as {@link InputStream}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link InputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream toInputStream(ByteBuffer buffer) throws IORuntimeException {
        return new ByteBufferInputStream(buffer);
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
     * Wraps given buffer as {@link Writer}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link Writer}
     * @throws IORuntimeException IO runtime exception
     */
    public static Writer toWriter(CharBuffer buffer) throws IORuntimeException {
        return new CharBufferWriter(buffer);
    }

    /**
     * Wraps given appendable as {@link OutputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Note {@link OutputStream#flush()} and {@link OutputStream#close()} are valid if given appendable is instance of
     * {@link Writer}.
     *
     * @param appendable given appendable
     * @return given appendable as {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream toOutputStream(Appendable appendable) throws IORuntimeException {
        return toOutputStream(appendable, JieChars.defaultCharset());
    }

    /**
     * Wraps given appendable as {@link OutputStream} with specified charset.
     * <p>
     * Note {@link OutputStream#flush()} and {@link OutputStream#close()} are valid if given appendable is instance of
     * {@link Writer}.
     *
     * @param appendable given appendable
     * @param charset    specified charset
     * @return given appendable as {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream toOutputStream(Appendable appendable, Charset charset) throws IORuntimeException {
        return new AppendableOutputStream(appendable, charset);
    }

    /**
     * Wraps given array as {@link OutputStream}.
     *
     * @param array given array
     * @return given array as {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream toOutputStream(byte[] array) throws IORuntimeException {
        return new BytesWrapperOutputStream(array, 0, array.length);
    }

    /**
     * Wraps given array as {@link OutputStream}, starting from given offset with specified length.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return given array as {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream toOutputStream(byte[] array, int offset, int length) throws IORuntimeException {
        JieCheck.checkRangeInBounds(offset, offset + length, 0, array.length);
        return new BytesWrapperOutputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as {@link OutputStream}.
     *
     * @param buffer given buffer
     * @return given buffer as {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream toOutputStream(ByteBuffer buffer) throws IORuntimeException {
        return new ByteBufferOutputStream(buffer);
    }

    /**
     * Limits given stream to a specified number for readable bytes. The returned stream doesn't support mark/reset
     * methods.
     *
     * @param stream given stream
     * @param number number of readable bytes, must &gt;= 0
     * @return limited {@link InputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream limit(InputStream stream, long number) throws IORuntimeException {
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
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream limit(OutputStream stream, long number) throws IORuntimeException {
        if (number < 0) {
            throw new IllegalArgumentException("number < 0.");
        }
        return new LimitedOutputStream(stream, number);
    }

    /**
     * Returns a {@link TransformInputStream} with source stream and transformer. This method is equivalent to
     * ({@link #transform(InputStream, int, Function)}):
     * <pre>
     *     return transform(source, 0, transformer);
     * </pre>
     *
     * @param source      source stream
     * @param transformer given transformer
     * @return a {@link TransformInputStream}
     * @see #transform(InputStream, int, Function)
     */
    public static TransformInputStream transform(InputStream source, Function<byte[], byte[]> transformer) {
        return transform(source, 0, transformer);
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
     * Wraps given random access file as an input stream, supports mark/reset.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @return wrapped stream
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream toInputStream(RandomAccessFile random) throws IORuntimeException {
        return toInputStream(random, 0, -1);
    }

    /**
     * Wraps given random access file as an input stream, readable bytes from {@code offset} position to
     * {@code (offset + length)}, supports mark/reset. {@code length} can be set to -1 if to read to end of the file.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @param offset offset position to start read
     * @param length length of readable bytes, or -1 to read to end of file
     * @return wrapped stream
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream toInputStream(RandomAccessFile random, long offset, long length) throws IORuntimeException {
        return new RandomInputStream(random, offset, length);
    }

    /**
     * Wraps given random access file as an output stream. The stream will lock the file with exclusive lock by
     * {@link FileChannel#tryLock(long, long, boolean)}.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @return wrapped stream
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream toOutputStream(RandomAccessFile random) throws IORuntimeException {
        return toOutputStream(random, 0, -1);
    }

    /**
     * Wraps given random access file as an output stream, written bytes from {@code offset} position to
     * {@code (offset + length)}. {@code length} can be set to -1 if to write unlimitedly. The stream will lock the file
     * with exclusive lock by {@link FileChannel#tryLock(long, long, boolean)}.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     * @param offset offset position to start write
     * @param length length of written bytes, or -1 to write unlimitedly
     * @return wrapped stream
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream toOutputStream(RandomAccessFile random, long offset, long length) throws IORuntimeException {
        return new RandomOutputStream(random, offset, length);
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
