package xyz.fsgek.common.data;

import xyz.fsgek.common.io.GekIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

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
    default T input(byte[] array) {
        return input(array, 0);
    }

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
     * Sets output to given array.
     *
     * @param array given array
     * @return this
     */
    default T output(byte[] array) {
        return output(array, 0);
    }

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
     * Starts data process, returns number of bytes written in output.
     *
     * @return number of bytes written in output
     */
    long start();

    /**
     * Starts data process, writes result into an array and returns.
     *
     * @return result array
     */
    default byte[] finalBytes() {
        return GekIO.read(finalStream());
    }

    /**
     * Returns an input stream which contains all configurations of current process, and will start process when call
     * read methods are called.
     *
     * @return an input stream which contains all configurations of current process, and will start process when call
     * read methods are called
     */
    InputStream finalStream();
}
