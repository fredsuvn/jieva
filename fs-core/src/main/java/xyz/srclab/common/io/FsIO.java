package xyz.srclab.common.io;

import xyz.srclab.build.annotations.FsMethods;
import xyz.srclab.common.base.FsCheck;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
            ByteArrayOutputStream out = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (true) {
                int readSize = inputStream.read(buffer);
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    out.write(buffer, 0, readSize);
                }
            }
            byte[] result = out.toByteArray();
            if (close) {
                inputStream.close();
            }
            return result;
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
        FsCheck.checkArgument(limit >= 0, "limit must >= 0");
        if (limit == 0) {
            return new byte[0];
        }
        try {
            int readNum = 0;
            int bufferSize = Math.min(limit, DEFAULT_BUFFER_SIZE);
            ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int readSize = inputStream.read(buffer, 0, Math.min(limit - readNum, buffer.length));
                if (readSize < 0) {
                    break;
                }
                if (readSize > 0) {
                    out.write(buffer, 0, readSize);
                    readNum += readSize;
                }
                int remaining = limit - readNum;
                if (remaining <= 0) {
                    break;
                }
            }
            byte[] result = out.toByteArray();
            if (close) {
                inputStream.close();
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException(e);
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
}
