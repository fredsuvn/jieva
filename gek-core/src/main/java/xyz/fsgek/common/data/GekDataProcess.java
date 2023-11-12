package xyz.fsgek.common.data;

import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.io.GekIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * This is base interface for data process.
 * It supports method chaining to configure and start a single data processing operation:
 * <pre>
 *     process.input(in).output(out).doFinal();
 * </pre>
 * Subtype needs to play the role of {@link T}, which represents subtype itself:
 * <pre>
 *     class SomeProcess implements GekDataProcess&lt;SomeProcess&gt;
 * </pre>
 *
 * @param <T> subtype of this interface, and is subtype itself
 * @author fredsuvn
 */
public interface GekDataProcess<T extends GekDataProcess<T>> {

    /**
     * Sets input to given array.
     *
     * @param array given array
     * @return this
     */
    T input(byte[] array);

    /**
     * Sets input to given array, starting from given offset to end of array.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    default T input(byte[] array, int offset) {
        return input(array, offset, array.length);
    }

    /**
     * Sets input to given array, starting from given offset to specified length.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return this
     */
    default T input(byte[] array, int offset, int length) {
        return input(ByteBuffer.wrap(array, offset, length));
    }

    /**
     * Sets input to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    T input(ByteBuffer buffer);

    /**
     * Sets input to given input stream.
     *
     * @param in given input stream
     * @return this
     */
    T input(InputStream in);

    /**
     * Sets input to given string with {@link GekChars#defaultCharset()}.
     *
     * @param str given string
     * @return this
     */
    default T input(String str) {
        return input(str, GekChars.defaultCharset());
    }

    /**
     * Sets input to given string with specified charset.
     *
     * @param str     given string
     * @param charset specified charset
     * @return this
     */
    default T input(String str, Charset charset) {
        return input(GekString.encode(str, charset));
    }

    /**
     * Sets input to given string with {@link GekChars#ISO_8859_1}.
     *
     * @param str given string
     * @return this
     */
    default T inputLatin(String str) {
        return input(GekString.encode(str, GekChars.ISO_8859_1));
    }

    /**
     * Sets output to given array.
     *
     * @param array given array
     * @return this
     */
    T output(byte[] array);

    /**
     * Sets output to given array, starting from given offset.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    default T output(byte[] array, int offset) {
        return output(ByteBuffer.wrap(array, offset, array.length - offset));
    }

    /**
     * Sets output to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    T output(ByteBuffer buffer);

    /**
     * Sets output to given output stream.
     *
     * @param out given output stream
     * @return this
     */
    T output(OutputStream out);

    /**
     * Starts and does final data process, returns number of bytes written in output.
     *
     * @return number of bytes written in output
     */
    long doFinal();

    /**
     * Starts and does final data process, returns int number of bytes written in output, or {@link Integer#MAX_VALUE}
     * if the number is greater than {@link Integer#MAX_VALUE}.
     *
     * @return int number of bytes written in output or {@link Integer#MAX_VALUE} if the number is greater than
     * {@link Integer#MAX_VALUE}.
     */
    default int doFinalInt() {
        long num = doFinal();
        return (int) (num > Integer.MAX_VALUE ? Integer.MAX_VALUE : num);
    }

    /**
     * Starts and does final data process, writes result into an array and returns.
     *
     * @return result array
     */
    default byte[] finalBytes() {
        return GekIO.read(finalStream());
    }

    /**
     * Returns an input stream which contains all configurations of current process,
     * and will start and do final data process when read methods are called.
     *
     * @return an input stream which contains all configurations of current process
     */
    InputStream finalStream();

    /**
     * Starts and does final data process, builds result as string with {@link GekChars#defaultCharset()} and returns.
     *
     * @return result as string with specified charset
     */
    default String finalString() {
        return finalString(GekChars.defaultCharset());
    }

    /**
     * Starts and does final data process, builds result as string with specified charset and returns.
     *
     * @param charset specified charset
     * @return result as string with specified charset
     */
    default String finalString(Charset charset) {
        return GekString.of(finalBytes(), charset);
    }

    /**
     * Starts and does final data process, builds result as string with {@link GekChars#ISO_8859_1} and returns.
     *
     * @return result as string with ISO_8859_1 charset
     */
    default String finalLatinString() {
        return finalString(GekChars.ISO_8859_1);
    }
}
