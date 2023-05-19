package xyz.srclab.common.io;

import xyz.srclab.build.annotations.FsMethods;
import xyz.srclab.common.base.FsCheck;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

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
     * Reads all bytes from given input stream, then close given input stream if given close is true (else not).
     *
     * @param inputStream given input stream
     * @param close       given close
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
     * Reads all bytes from given input stream to given dest stream.
     *
     * @param inputStream given input stream
     * @param dest        given dest stream
     */
    public static void readBytesTo(InputStream inputStream, OutputStream dest) {
        try {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (true) {
                int readSize = inputStream.read(buffer);
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    dest.write(buffer, 0, readSize);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads specified number of bytes from given input stream,
     * then close given input stream if given close is true (else not).
     * <p>
     * The number of return read bytes may be less than specified number
     * if given input stream doesn't have enough bytes to read.
     *
     * @param inputStream given input stream
     * @param limit       specified number
     * @param close       given close
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
     * Reads specified number of bytes from given input stream to given dest stream.
     * <p>
     * The number of return read bytes may be less than specified number
     * if given input stream doesn't have enough bytes to read.
     *
     * @param inputStream given input stream
     * @param dest        given dest stream
     * @param limit       specified number
     */
    public static void readBytesTo(InputStream inputStream, OutputStream dest, int limit) {
        FsCheck.checkArgument(limit >= 0, "limit must >= 0");
        if (limit == 0) {
            return;
        }
        try {
            int readNum = 0;
            int bufferSize = Math.min(limit, DEFAULT_BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int readSize = inputStream.read(buffer, 0, Math.min(limit - readNum, buffer.length));
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    dest.write(buffer, 0, readSize);
                    readNum += readSize;
                }
                int remaining = limit - readNum;
                if (remaining <= 0) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads all chars from given reader, then close given reader if given close is true (else not).
     *
     * @param reader given reader
     * @param close  given close
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
     * Reads all chars from given reader to given dest.
     *
     * @param reader given reader
     * @param dest   given dest
     */
    public static void readCharsTo(Reader reader, Appendable dest) {
        try {
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            while (true) {
                int readSize = reader.read(buffer);
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    append(dest, buffer, 0, readSize);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads specified number of chars from given input reader as String,
     * then close given reader if given close is true (else not).
     * <p>
     * The number of return read chars may be less than specified number
     * if given input stream doesn't have enough chars to read.
     *
     * @param reader given reader
     * @param limit  specified number
     * @param close  given close
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
     * Reads specified number of chars from given reader to given dest.
     * <p>
     * The number of return read chars may be less than specified number
     * if given input reader doesn't have enough chars to read.
     *
     * @param reader given reader
     * @param dest   given dest
     * @param limit  specified number
     */
    public static void readCharsTo(Reader reader, Appendable dest, int limit) {
        FsCheck.checkArgument(limit >= 0, "limit must >= 0");
        if (limit == 0) {
            return;
        }
        try {
            int readNum = 0;
            int bufferSize = Math.min(limit, DEFAULT_BUFFER_SIZE);
            char[] buffer = new char[bufferSize];
            while (true) {
                int readSize = reader.read(buffer, 0, Math.min(limit - readNum, buffer.length));
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    append(dest, buffer, 0, readSize);
                    readNum += readSize;
                }
                int remaining = limit - readNum;
                if (remaining <= 0) {
                    break;
                }
            }
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
}
