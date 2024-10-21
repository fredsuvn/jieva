package xyz.fslabo.common.io;

import xyz.fslabo.common.base.JieChars;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Provides implementations and utilities for {@link InputStream}/{@link Reader}.
 *
 * @author sunqian
 */
public class JieInput {

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
    public static InputStream wrap(byte[] array) {
        return InImpls.ofBytes(array);
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
    public static InputStream wrap(byte[] array, int offset, int length) {
        return InImpls.ofBytes(array, offset, length);
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
    public static InputStream wrap(ByteBuffer buffer) {
        return InImpls.ofBuffer(buffer);
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
    public static InputStream wrap(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        try {
            return InImpls.ofRandom(random, initialSeek);
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
    public static InputStream wrap(Reader reader) {
        return wrap(reader, JieChars.defaultCharset());
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
    public static InputStream wrap(Reader reader, Charset charset) {
        return InImpls.ofReader(reader, charset);
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
    public static Reader wrap(char[] array) {
        return InImpls.ofChars(array);
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
    public static Reader wrap(char[] array, int offset, int length) {
        return InImpls.ofChars(array, offset, length);
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
    public static Reader wrap(CharSequence chars) {
        return InImpls.ofChars(chars);
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
    public static Reader wrap(CharBuffer buffer) {
        return InImpls.ofChars(buffer);
    }
}
