package xyz.srclab.common.io;

import org.apache.commons.io.output.WriterOutputStream;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.build.annotations.FsMethods;
import xyz.srclab.common.base.FsDefault;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Cache interface and static methods.
 *
 * @author fresduvn
 */
@FsMethods
public class FsIO {

    /**
     * Default buffer size.
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * Reads all bytes from given input stream.
     *
     * @param inputStream given input stream
     */
    public static byte[] readBytes(InputStream inputStream) {
        return readBytes(inputStream, false);
    }

    /**
     * Reads all bytes from given input stream, then close the stream if given close is true (else not).
     *
     * @param inputStream given input stream
     * @param close       whether close the stream after reading
     */
    public static byte[] readBytes(InputStream inputStream, boolean close) {
        try {
            ByteArrayOutputStream dest = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
            readBytesTo(inputStream, dest);
            if (close) {
                inputStream.close();
            }
            return dest.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads specified limit number of bytes from given input stream.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading bytes until it reaches the limit or the end of the stream.
     *
     * @param inputStream given input stream
     * @param limit       specified limit number
     */
    public static byte[] readBytes(InputStream inputStream, int limit) {
        return readBytes(inputStream, limit, false);
    }

    /**
     * Reads specified limit number of bytes from given input stream,
     * then close the stream if given close is true (else not).
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading bytes until it reaches the limit or the end of the stream.
     *
     * @param inputStream given input stream
     * @param limit       specified limit number
     * @param close       whether close the stream after reading
     */
    public static byte[] readBytes(InputStream inputStream, int limit, boolean close) {
        try {
            ByteArrayOutputStream dest = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
            readBytesTo(inputStream, dest, limit);
            if (close) {
                inputStream.close();
            }
            return dest.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads all bytes from given input stream to given dest stream, returns actual read number.
     *
     * @param inputStream given input stream
     * @param dest        given dest stream
     */
    public static long readBytesTo(InputStream inputStream, OutputStream dest) {
        return readBytesTo(inputStream, dest, -1);
    }

    /**
     * Reads specified limit number of bytes from given input stream to given dest stream, returns actual read number.
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
        if (limit == 0) {
            return 0;
        }
        try {
            long readNum = 0;
            int bufferSize = limit < 0 ? DEFAULT_BUFFER_SIZE : Math.min(limit, DEFAULT_BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int readLen = limit < 0 ? buffer.length : (int) Math.min(limit - readNum, buffer.length);
                int readSize = inputStream.read(buffer, 0, readLen);
                if (readSize < 0) {
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
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads available bytes from given input stream, or null if reaches end of the stream.
     *
     * @param inputStream given input stream
     */
    @Nullable
    public static byte[] availableBytes(InputStream inputStream) {
        try {
            int available = inputStream.available();
            if (available < 0) {
                return null;
            }
            if (available == 0) {
                return new byte[0];
            }
            byte[] bytes = new byte[available];
            int off = 0;
            int remain = bytes.length;
            while (remain > 0) {
                int actual = inputStream.read(bytes, off, remain);
                if (actual == -1) {
                    if (off == 0) {
                        return null;
                    }
                    return Arrays.copyOfRange(bytes, 0, off);
                }
                remain -= actual;
                off += actual;
            }
            return bytes;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads available bytes from given input stream to given dest stream,
     * returns actual read number (-1 if reaches end of the stream).
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
                return 0;
            }
            return readBytesTo(inputStream, dest, available);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads all \chars from given reader.
     *
     * @param reader given reader
     */
    public static String readString(Reader reader) {
        return readString(reader, false);
    }

    /**
     * Reads all \chars from given reader, then close the reader if given close is true (else not).
     *
     * @param reader given reader
     * @param close  whether close the stream after reading
     */
    public static String readString(Reader reader, boolean close) {
        try {
            StringBuilder dest = new StringBuilder();
            readCharsTo(reader, dest);
            if (close) {
                reader.close();
            }
            return dest.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads specified limit number of chars from given reader.
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading chars until it reaches the limit or the end of the reader.
     *
     * @param reader given reader
     * @param limit  specified limit number
     */
    public static String readString(Reader reader, int limit) {
        return readString(reader, limit, false);
    }

    /**
     * Reads specified limit number of chars from given reader,
     * then close the reader if given close is true (else not).
     * <p>
     * If the limit number &lt; 0, read all bytes;
     * els if limit number is 0, no read and return;
     * else this method will keep reading chars until it reaches the limit or the end of the reader.
     *
     * @param reader given reader
     * @param limit  specified limit number
     * @param close  whether close the stream after reading
     */
    public static String readString(Reader reader, int limit, boolean close) {
        try {
            StringBuilder dest = new StringBuilder();
            readCharsTo(reader, dest, limit);
            if (close) {
                reader.close();
            }
            return dest.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads all chars from given reader to given dest output, returns actual read number.
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
        if (limit == 0) {
            return 0;
        }
        try {
            long readNum = 0;
            int bufferSize = limit < 0 ? DEFAULT_BUFFER_SIZE : Math.min(limit, DEFAULT_BUFFER_SIZE);
            char[] buffer = new char[bufferSize];
            while (true) {
                int readLen = limit < 0 ? buffer.length : (int) Math.min(limit - readNum, buffer.length);
                int readSize = reader.read(buffer, 0, readLen);
                if (readSize < 0) {
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
            throw new IllegalStateException(e);
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
     * Reads String encoded by all bytes from given input stream with {@link FsDefault#charset()}.
     *
     * @param inputStream given input stream
     */
    public static String readString(InputStream inputStream) {
        return readString(inputStream, FsDefault.charset());
    }

    /**
     * Reads String encoded by all bytes from given input stream.
     *
     * @param inputStream given input stream
     * @param charset     charset of the string
     */
    public static String readString(InputStream inputStream, Charset charset) {
        return new String(readBytes(inputStream), charset);
    }

    /**
     * Reads String encoded by all bytes from given input stream,
     * then close given input stream if given close is true (else not).
     *
     * @param inputStream given input stream
     * @param charset     charset of the string
     * @param close       given close
     */
    public static String readString(InputStream inputStream, Charset charset, boolean close) {
        return new String(readBytes(inputStream, close), charset);
    }

    /**
     * Reads available String encoded by all bytes from given input stream with {@link FsDefault#charset()},
     * returns null if reaches end of the stream.
     *
     * @param inputStream given input stream
     */
    @Nullable
    public static String avalaibleString(InputStream inputStream) {
        return avalaibleString(inputStream, FsDefault.charset());
    }

    /**
     * Reads available String encoded by all bytes from given input stream,
     * returns null if reaches end of the stream.
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
     * Returns a reader of which content from given input stream.
     *
     * @param inputStream given input stream
     * @param charset     charset of return reader
     */
    public static Reader toReader(InputStream inputStream, Charset charset) {
        return new InputStreamReader(inputStream, charset);
    }

    /**
     * Returns an input stream of which content from given reader.
     *
     * @param reader  given reader
     * @param charset charset of given reader
     */
    public static InputStream toInputStream(Reader reader, Charset charset) {
        return new ReaderInputStream(reader, charset);
    }

    /**
     * Wraps given output stream as a writer.
     *
     * @param outputStream given out stream
     * @param charset      charset writer
     */
    public static Writer toWriter(OutputStream outputStream, Charset charset) {
        return new OutputStreamWriter(outputStream, charset);
    }

    /**
     * Wraps given writer as an output stream.
     *
     * @param writer  given writer
     * @param charset charset of writer
     */
    public static OutputStream toOutputStream(Writer writer, Charset charset) {
        return new WriterOutputStream(writer, charset);
    }

    /**
     * Returns a reader of which content from given buffer.
     *
     * @param buffer given buffer
     */
    public static Reader toReader(CharBuffer buffer) {
        return new CharBufferReader(buffer);
    }

    /**
     * Returns an input stream of which content from given buffer.
     *
     * @param buffer given buffer
     */
    public static InputStream toInputStream(ByteBuffer buffer) {
        return new ByteBufferInputStream(buffer);
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
     * Wraps given buffer as an output stream.
     *
     * @param buffer given writer
     */
    public static OutputStream toOutputStream(ByteBuffer buffer) {
        return new ByteBufferOutputStream(buffer);
    }
}
