package xyz.srclab.common.io;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsString;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Input/Output utilities.
 *
 * @author fresduvn
 */
public class FsIO {

    /**
     * Default IO buffer size: 1024 * 8 = 8192.
     */
    public static final int IO_BUFFER_SIZE = 1024 * 8;

    /**
     * Reads all bytes from given input stream.
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     */
    @Nullable
    public static byte[] readBytes(InputStream inputStream) {
        return readBytes(inputStream, false);
    }

    /**
     * Reads all bytes from given input stream, then close the stream if given close is true (else not).
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     * @param close       whether close the stream after reading
     */
    @Nullable
    public static byte[] readBytes(InputStream inputStream, boolean close) {
        try {
            ByteArrayOutputStream dest = new ByteArrayOutputStream();
            long readCount = readBytesTo(inputStream, dest);
            if (readCount == -1) {
                return null;
            }
            if (close) {
                inputStream.close();
            }
            return dest.toByteArray();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Reads specified limit number of bytes from given input stream.
     * Return null if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading bytes until it reaches the limit or the end of the stream.
     *
     * @param inputStream given input stream
     * @param limit       specified limit number
     */
    @Nullable
    public static byte[] readBytes(InputStream inputStream, int limit) {
        return readBytes(inputStream, limit, false);
    }

    /**
     * Reads specified limit number of bytes from given input stream,
     * then close the stream if given close is true (else not).
     * Return null if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading bytes until it reaches the limit or the end of the stream.
     *
     * @param inputStream given input stream
     * @param limit       specified limit number
     * @param close       whether close the stream after reading
     */
    @Nullable
    public static byte[] readBytes(InputStream inputStream, int limit, boolean close) {
        try {
            ByteArrayOutputStream dest = new ByteArrayOutputStream();
            long readCount = readBytesTo(inputStream, dest, limit);
            if (readCount == -1) {
                return null;
            }
            if (close) {
                inputStream.close();
            }
            return dest.toByteArray();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Reads all bytes from given input stream to given dest stream, returns actual read number.
     * Return -1 if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     * @param dest        given dest stream
     */
    public static long readBytesTo(InputStream inputStream, OutputStream dest) {
        return readBytesTo(inputStream, dest, -1);
    }

    /**
     * Reads specified limit number of bytes from given input stream to given dest stream, returns actual read number.
     * Return -1 if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading bytes until it reaches the limit or the end of the stream.
     *
     * @param inputStream given input stream
     * @param dest        given dest stream
     * @param limit       specified limit number
     */
    public static long readBytesTo(InputStream inputStream, OutputStream dest, int limit) {
        return readBytesTo(inputStream, dest, limit, IO_BUFFER_SIZE);
    }

    /**
     * Reads specified limit number of bytes from given input stream to given dest stream, returns actual read number.
     * Return -1 if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading bytes until it reaches the limit or the end of the stream.
     *
     * @param inputStream given input stream
     * @param dest        given dest stream
     * @param limit       specified limit number
     * @param bufferSize  buffer size for each reading and writing
     */
    public static long readBytesTo(InputStream inputStream, OutputStream dest, int limit, int bufferSize) {
        if (limit == 0) {
            return 0;
        }
        try {
            long readNum = 0;
            int actualBufferSize = limit < 0 ? bufferSize : Math.min(limit, bufferSize);
            byte[] buffer = new byte[actualBufferSize];
            while (true) {
                int readLen = limit < 0 ? buffer.length : (int) Math.min(limit - readNum, buffer.length);
                int readSize = inputStream.read(buffer, 0, readLen);
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
                if (limit < 0) {
                    continue;
                }
                long remaining = limit - readNum;
                if (remaining <= 0) {
                    break;
                }
            }
            return readNum;
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Reads available bytes from given input stream.
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     */
    @Nullable
    public static byte[] availableBytes(InputStream inputStream) {
        try {
            ByteArrayOutputStream dest = new ByteArrayOutputStream();
            long readCount = availableBytesTo(inputStream, dest);
            if (readCount == -1) {
                return null;
            }
            return dest.toByteArray();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Reads available bytes from given input stream to given dest stream,
     * returns actual read number.
     * Return -1 if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     * @param dest        given dest stream
     */
    public static long availableBytesTo(InputStream inputStream, OutputStream dest) {
        try {
            int available = inputStream.available();
            if (available < 0) {
                return -1;
            }
            if (available == 0) {
                return readBytesTo(inputStream, dest, 1);
            }
            return readBytesTo(inputStream, dest, available);
        } catch (IOException e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Reads all chars from given reader.
     * Return null if no data read out and reach to the end of stream.
     *
     * @param reader given reader
     */
    @Nullable
    public static String readString(Reader reader) {
        return readString(reader, false);
    }

    /**
     * Reads all chars from given reader, then close the reader if given close is true (else not).
     * Return null if no data read out and reach to the end of stream.
     *
     * @param reader given reader
     * @param close  whether close the stream after reading
     */
    @Nullable
    public static String readString(Reader reader, boolean close) {
        try {
            StringBuilder dest = new StringBuilder();
            long readCount = readCharsTo(reader, dest);
            if (readCount == -1) {
                return null;
            }
            if (close) {
                reader.close();
            }
            return dest.toString();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Reads specified limit number of chars from given reader.
     * Return null if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading chars until it reaches the limit or the end of the reader.
     *
     * @param reader given reader
     * @param limit  specified limit number
     */
    @Nullable
    public static String readString(Reader reader, int limit) {
        return readString(reader, limit, false);
    }

    /**
     * Reads specified limit number of chars from given reader,
     * then close the reader if given close is true (else not).
     * Return null if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading chars until it reaches the limit or the end of the reader.
     *
     * @param reader given reader
     * @param limit  specified limit number
     * @param close  whether close the stream after reading
     */
    @Nullable
    public static String readString(Reader reader, int limit, boolean close) {
        try {
            StringBuilder dest = new StringBuilder();
            long readCount = readCharsTo(reader, dest, limit);
            if (readCount == -1) {
                return null;
            }
            if (close) {
                reader.close();
            }
            return dest.toString();
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Reads all chars from given reader to given dest output, returns actual read number.
     * Return -1 if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all chars;
     * els if limit number is 0, no read and return;
     * else this method will keep reading chars until it reaches the limit or the end of the reader.
     *
     * @param reader given reader
     * @param dest   given dest stream
     */
    public static long readCharsTo(Reader reader, Appendable dest) {
        return readCharsTo(reader, dest, -1);
    }

    /**
     * Reads specified limit number of chars from given reader to given dest output, returns actual read number.
     * Return -1 if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all chars;
     * els if limit number is 0, no read and return;
     * else this method will keep reading chars until it reaches the limit or the end of the reader.
     *
     * @param reader given reader
     * @param dest   given dest stream
     * @param limit  specified limit number
     */
    public static long readCharsTo(Reader reader, Appendable dest, int limit) {
        return readCharsTo(reader, dest, limit, IO_BUFFER_SIZE);
    }

    /**
     * Reads specified limit number of chars from given reader to given dest output, returns actual read number.
     * Return -1 if no data read out and reach to the end of stream.
     * <p>
     * If the limit number &lt; 0, read all chars;
     * els if limit number is 0, no read and return;
     * else this method will keep reading chars until it reaches the limit or the end of the reader.
     *
     * @param reader     given reader
     * @param dest       given dest stream
     * @param limit      specified limit number
     * @param bufferSize buffer size for each reading and writing
     */
    public static long readCharsTo(Reader reader, Appendable dest, int limit, int bufferSize) {
        if (limit == 0) {
            return 0;
        }
        try {
            long readNum = 0;
            int actualBufferSize = limit < 0 ? bufferSize : Math.min(limit, bufferSize);
            char[] buffer = new char[actualBufferSize];
            while (true) {
                int readLen = limit < 0 ? buffer.length : (int) Math.min(limit - readNum, buffer.length);
                int readSize = reader.read(buffer, 0, readLen);
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
                if (limit < 0) {
                    continue;
                }
                long remaining = limit - readNum;
                if (remaining <= 0) {
                    break;
                }
            }
            return readNum;
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    private static void append(Appendable dest, char[] chars, int off, int len) throws IOException {
        if (dest instanceof StringBuilder) {
            ((StringBuilder) dest).append(chars, off, len);
        } else if (dest instanceof StringBuffer) {
            ((StringBuffer) dest).append(chars, off, len);
        } else if (dest instanceof Writer) {
            ((Writer) dest).write(chars, off, len);
        } else {
            dest.append(new String(chars, off, len));
        }
    }

    /**
     * Reads String encoded by all bytes from given input stream with {@link FsString#CHARSET}.
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     */
    @Nullable
    public static String readString(InputStream inputStream) {
        return readString(inputStream, FsString.CHARSET);
    }

    /**
     * Reads String encoded by all bytes from given input stream.
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     * @param charset     charset of the string
     */
    @Nullable
    public static String readString(InputStream inputStream, Charset charset) {
        byte[] bytes = readBytes(inputStream);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    /**
     * Reads String encoded by all bytes from given input stream,
     * then close given input stream if given close is true (else not).
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     * @param charset     charset of the string
     * @param close       given close
     */
    @Nullable
    public static String readString(InputStream inputStream, Charset charset, boolean close) {
        byte[] bytes = readBytes(inputStream, close);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    /**
     * Reads available String encoded by all bytes from given input stream with
     * {@link FsString#CHARSET}, returns null if reaches end of the stream.
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     */
    @Nullable
    public static String avalaibleString(InputStream inputStream) {
        return avalaibleString(inputStream, FsString.CHARSET);
    }

    /**
     * Reads available String encoded by all bytes from given input stream,
     * Return null if no data read out and reach to the end of stream.
     *
     * @param inputStream given input stream
     * @param charset     charset of the string
     */
    @Nullable
    public static String avalaibleString(InputStream inputStream, Charset charset) {
        byte[] bytes = availableBytes(inputStream);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    /**
     * Wraps given input stream as a reader with {@link InputStreamReader} and {@link FsString#CHARSET}.
     *
     * @param inputStream given input stream
     */
    public static Reader toReader(InputStream inputStream) {
        return toReader(inputStream, FsString.CHARSET);
    }

    /**
     * Wraps given input stream as a reader with {@link InputStreamReader}.
     *
     * @param inputStream given input stream
     * @param charset     charset of return reader
     */
    public static Reader toReader(InputStream inputStream, Charset charset) {
        return new InputStreamReader(inputStream, charset);
    }

    /**
     * Wraps given buffer as a reader, supports mark/reset.
     *
     * @param buffer given buffer
     */
    public static Reader toReader(CharBuffer buffer) {
        return new CharBufferReader(buffer);
    }

    /**
     * Wraps given reader as an input stream with {@link FsString#CHARSET}, doesn't support mark/reset.
     *
     * @param reader given reader
     */
    public static InputStream toInputStream(Reader reader) {
        return toInputStream(reader, FsString.CHARSET);
    }

    /**
     * Wraps given reader as an input stream, doesn't support mark/reset.
     *
     * @param reader  given reader
     * @param charset charset of given reader
     */
    public static InputStream toInputStream(Reader reader, Charset charset) {
        return new ReaderInputStream(reader, charset);
    }

    /**
     * Wraps given buffer as an input stream, supports mark/reset.
     *
     * @param buffer given buffer
     */
    public static InputStream toInputStream(ByteBuffer buffer) {
        return new ByteBufferInputStream(buffer);
    }

    /**
     * Wraps given random access file as an input stream, supports mark/reset.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     */
    public static InputStream toInputStream(RandomAccessFile random) {
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
     */
    public static InputStream toInputStream(RandomAccessFile random, long offset, long length) {
        return new RandomInputStream(random, offset, length);
    }

    /**
     * Wraps given output stream as a writer with {@link OutputStreamWriter} and {@link FsString#CHARSET}.
     *
     * @param outputStream given out stream
     */
    public static Writer toWriter(OutputStream outputStream) {
        return toWriter(outputStream, FsString.CHARSET);
    }

    /**
     * Wraps given output stream as a writer with {@link OutputStreamWriter}.
     *
     * @param outputStream given out stream
     * @param charset      charset writer
     */
    public static Writer toWriter(OutputStream outputStream, Charset charset) {
        return new OutputStreamWriter(outputStream, charset);
    }

    /**
     * Wraps given buffer as a writer.
     *
     * @param buffer given buffer
     */
    public static Writer toWriter(CharBuffer buffer) {
        return new CharBufferWriter(buffer);
    }

    /**
     * Wraps given appendable as an output stream with {@link FsString#CHARSET}.
     * <p>
     * Note {@link OutputStream#flush()} and {@link OutputStream#close()} are valid for given {@code appendable}
     * if it is instance of {@link Writer}.
     *
     * @param appendable given appendable
     */
    public static OutputStream toOutputStream(Appendable appendable) {
        return toOutputStream(appendable, FsString.CHARSET);
    }

    /**
     * Wraps given appendable as an output stream.
     * <p>
     * Note {@link OutputStream#flush()} and {@link OutputStream#close()} are valid for given {@code appendable}
     * if it is instance of {@link Writer}.
     *
     * @param appendable given appendable
     * @param charset    charset of writer
     */
    public static OutputStream toOutputStream(Appendable appendable, Charset charset) {
        return new AppendableOutputStream(appendable, charset);
    }

    /**
     * Wraps given buffer as an output stream.
     *
     * @param buffer given writer
     */
    public static OutputStream toOutputStream(ByteBuffer buffer) {
        return new ByteBufferOutputStream(buffer);
    }

    /**
     * Wraps given random access file as an output stream.
     * The stream will lock the file with exclusive lock by {@link FileChannel#tryLock(long, long, boolean)}.
     * <p>
     * Note this method will seek position of random access file to given offset immediately.
     *
     * @param random given random access file
     */
    public static OutputStream toOutputStream(RandomAccessFile random) {
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
     */
    public static OutputStream toOutputStream(RandomAccessFile random, long offset, long length) {
        return new RandomOutputStream(random, offset, length);
    }

    /**
     * Wraps given source stream to limit read length.
     * Note returned stream doesn't support mark/reset.
     *
     * @param source given source stream
     * @param limit  max read length, must >= 0
     */
    public static InputStream limit(InputStream source, long limit) {
        return new LimitedInputStream(source, limit);
    }

    /**
     * Wraps given source stream to limit written length.
     *
     * @param source given source stream
     * @param limit  max written length, must >= 0
     */
    public static OutputStream limit(OutputStream source, long limit) {
        return new LimitedOutputStream(source, limit);
    }

    /**
     * Read all bytes of given buffer into a new byte array and return.
     *
     * @param buffer given buffer
     */
    public static byte[] toByteArray(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }
}
